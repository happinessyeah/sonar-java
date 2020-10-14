/*
 * SonarQube Java
 * Copyright (C) 2012-2020 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

import java.io.File;
import java.util.LinkedList;

public class MyCheckTest {

  @Test
  public void test() {

    // Use an instance of the check under test to raise the issue.
    MyRuleCheck check = new MyRuleCheck();

    // Verifies that the check will raise the adequate issues with the expected message.
    // In the test file, lines which should raise an issue have been commented out
    // by using the following syntax: "// Noncompliant {{EXPECTED_MESSAGE}}"
    JavaCheckVerifier.newVerifier()
      .onFile("src/test/files/Tree.java")
      .withCheck(check)
      .verifyIssues();
  }


  public void check(String filepath) {
    System.out.println("接收到的文件名称" + filepath);
    MyRuleCheck check = new MyRuleCheck();

    // Verifies that the check will raise the adequate issues with the expected message.
    // In the test file, lines which should raise an issue have been commented out
    // by using the following syntax: "// Noncompliant {{EXPECTED_MESSAGE}}"
    JavaCheckVerifier.newVerifier()
      .onFile(filepath)
      .withCheck(check)
      .verifyIssues();
  }

  @Test
  public void testFile() {
    String path = "C:\\Users\\m\\Desktop\\test\\2020090909591411919220f949b334dd192e774f636dc3673\\tangdao\\";    //要遍历的路径
    File file = new File(path);    //获取其file对象
    int fileNum = 0, folderNum = 0;
    if (file.exists()) {
      LinkedList<File> list = new LinkedList<File>();
      File[] files = file.listFiles();
      for (File file2 : files) {
        if (file2.isDirectory()) {
          System.out.println("文件夹:" + file2.getAbsolutePath());
          list.add(file2);
          folderNum++;
        } else {
          String absolutePath = file2.getAbsolutePath();
          System.out.println("文件:" + absolutePath);
          if (absolutePath.endsWith(".java")) {
            check(absolutePath);
          }
          fileNum++;
        }
      }
      File temp_file;
      while (!list.isEmpty()) {
        temp_file = list.removeFirst();
        files = temp_file.listFiles();
        for (File file2 : files) {
          if (file2.isDirectory()) {
            System.out.println("文件夹:" + file2.getAbsolutePath());
            list.add(file2);
            folderNum++;
          } else {
            String absolutePath = file2.getAbsolutePath();
            System.out.println("文件:" + absolutePath);
            if (absolutePath.endsWith(".java")) {
              check(absolutePath);
            }

            fileNum++;
          }
        }
      }
    } else {
      System.out.println("文件不存在!");
    }
    System.out.println("文件夹共有:" + folderNum + ",文件共有:" + fileNum);
  }
}
