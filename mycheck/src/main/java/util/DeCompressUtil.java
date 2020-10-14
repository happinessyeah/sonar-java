package util;

import okio.BufferedSink;
import okio.Okio;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;


/**
 * @author jimiao
 * @date 2019/9/12
 * 解压zip rar
 */
public class DeCompressUtil {

  private static Log log = LogFactory.getLog(DeCompressUtil.class);

  public static void unZip(File zipFile, String outDir) throws IOException {
    File outFileDir = new File(outDir);
    if (!outFileDir.exists()) {
      boolean isMakDir = outFileDir.mkdirs();
      if (!isMakDir) {
        log.error("\n====>文件解压:创建文件解压目录失败,文件路径:" + outFileDir);
      }
    }

    ZipFile zip = new ZipFile(zipFile);
    for (Enumeration enumeration = zip.getEntries(); enumeration.hasMoreElements(); ) {
      ZipEntry entry = (ZipEntry) enumeration.nextElement();
      String zipEntryName = entry.getName();
      InputStream in = zip.getInputStream(entry);

      if (entry.isDirectory()) {      //处理压缩文件包含文件夹的情况
        // 处理压缩文件包含文件夹的情况
        String pathname = outDir + zipEntryName;
        File fileDir = new File(pathname);
        fileDir.mkdirs();
        continue;
      }
      if (zipEntryName.startsWith(".")) {
        continue;
      }
      // 匹配文件是以点开头的隐藏文件
      String realFileName = zipEntryName.substring(zipEntryName.lastIndexOf("/") + 1);
      if (realFileName.startsWith(".")) {
        continue;
      }
      // 匹配文件是以点开头的隐藏文件夹
      String matchFileName = zipEntryName.substring(zipEntryName.indexOf("/") + 1);
      if (matchFileName.startsWith(".")) {
        continue;
      }
      String pathName = outDir + zipEntryName;
      File file = new File(pathName);
      if (!file.getParentFile().exists()) {
        file.getParentFile().mkdirs();
      }
      if (file.createNewFile()) {
        try (OutputStream out = new FileOutputStream(file)) {
          byte[] buff = new byte[1024];
          int len;
          while ((len = in.read(buff)) > 0) {
            out.write(buff, 0, len);
          }
        } catch (Exception e) {
          log.error("\n====>文件解压:发生异常,可能导致检测失败{}", e);
        } finally {
          if (in != null) {
            in.close();
          }
        }
      }
    }
    if (zip != null) {
      zip.close();
    }
  }

  /**
   * 解压zip文件并返回解压后文件夹路径名称
   *
   * @param zipFile
   * @param outDir
   * @return
   * @throws IOException
   */
  public static String unZipString(File zipFile, String outDir) throws IOException {
    unZip(zipFile, outDir);
    return outDir;
  }

  public static String okioUnZipString(File zipFile, String outDir) throws IOException {
    okioUnZip(zipFile, outDir);
    return outDir;
  }

  public static void okioUnZip(File zipFile, String outDir) throws IOException {
    File outFileDir = new File(outDir);
    if (!outFileDir.exists()) {
      boolean isMakDir = outFileDir.mkdirs();
      if (!isMakDir) {
        log.error("\n====>文件解压:创建文件解压目录失败,文件路径:" + outFileDir);
      }
    }

    ZipFile zip = new ZipFile(zipFile);
    for (Enumeration enumeration = zip.getEntries(); enumeration.hasMoreElements(); ) {
      ZipEntry entry = (ZipEntry) enumeration.nextElement();
      String zipEntryName = entry.getName();
      InputStream in = zip.getInputStream(entry);

      if (entry.isDirectory()) {      //处理压缩文件包含文件夹的情况
        // 处理压缩文件包含文件夹的情况
        String pathname = outDir + zipEntryName;
        File fileDir = new File(pathname);
        fileDir.mkdirs();
        continue;
      }
      if (zipEntryName.startsWith(".")) {
        continue;
      }
      // 匹配文件是以点开头的隐藏文件
      String realFileName = zipEntryName.substring(zipEntryName.lastIndexOf("/") + 1);
      if (realFileName.startsWith(".")) {
        continue;
      }
      // 匹配文件是以点开头的隐藏文件夹
      String matchFileName = zipEntryName.substring(zipEntryName.indexOf("/") + 1);
      if (matchFileName.startsWith(".")) {
        continue;
      }
      String pathName = outDir + zipEntryName;
      File file = new File(pathName);
      if (!file.getParentFile().exists()) {
        file.getParentFile().mkdirs();
      }
      if (file.createNewFile()) {
        try (BufferedSink sink = Okio.buffer(Okio.sink(file))) {
          String content = Okio.buffer(Okio.source(in)).readString(Charset.forName("utf8"));
          sink.writeUtf8(content);
        } catch (Exception e) {
          log.error("\n====>文件解压:发生异常,可能导致检测失败{}", e);
        } finally {
          if (in != null) {
            in.close();
          }
        }
      }
    }
    if (zip != null) {
      zip.close();
    }
  }
}
