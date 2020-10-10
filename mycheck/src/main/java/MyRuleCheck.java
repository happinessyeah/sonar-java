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

import org.sonar.java.checks.helpers.ExpressionsHelper;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.semantic.MethodMatchers;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.BlockTree;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.ImportTree;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.Tree;

public class MyRuleCheck extends BaseTreeVisitor implements JavaFileScanner {

  private JavaFileScannerContext context;

  @Override
  public void scanFile(JavaFileScannerContext context) {
    System.out.println("进入scanFile方法...");
    this.context = context;

    scan(context.getTree());

//    System.out.println("这是规则中的输出：" + PrinterVisitor.print(context.getTree()));
  }

  @Override
  public void visitMethod(MethodTree tree) {
    System.out.println("-----------visitMethod-------------------");
    BlockTree blockTree = tree.block();
    if (blockTree != null) {
      //方法体开始行数
      int line = tree.block().openBraceToken().line();
      //方法体结束行数
      int line1 = tree.block().closeBraceToken().line();
      System.out.println("进入visitMethod方法..." + tree.simpleName() + "," + line + "," + line1);
    } else {
      System.out.println("method is null");
    }
    super.visitMethod(tree);
  }


  @Override
  public void visitImport(ImportTree tree) {
    System.out.println("------------------------------");
    System.out.println("进入visitImport方法...");
    // 获取import的内容
    String fullyQualifiedName = ExpressionsHelper.concatenate((ExpressionTree) tree.qualifiedIdentifier());
//    SyntaxToken syntaxToken = tree.semicolonToken();
//    String text = syntaxToken.text();
//    int line = syntaxToken.line();
    System.out.println("import的内容如下：" + fullyQualifiedName);
    super.visitImport(tree);
  }


  private static final MethodMatchers TEST_METHOD = MethodMatchers.create()
    .ofSubTypes("java.util.regex.Matcher")
    .names("matches")
    .withAnyParameters()
    .build();


  /**
   * visitMethodInvocation 指对类中调用方法进行规则，通过它可以对项目中禁止使用的方法进行检测，对调用的方法进行检测
   *
   * @param tree
   */
  @Override
  public void visitMethodInvocation(MethodInvocationTree tree) {
    System.out.println("------------visitMethodInvocation------------------");
    //获取调用的方法名称
    String concatenate = ExpressionsHelper.concatenate(tree.methodSelect());
    // 获取调用的方法所在行数
    int line = tree.firstToken().line();
    // 获取调用的方法所在块开始和结束行数
    Tree parentTree = tree.parent();
    int line1 = parentTree.firstToken().line();
    int line2 = parentTree.lastToken().line();
    System.out.println("进入visitMethodInvocation方法..." + concatenate + "," + line + "," + line1 + "," + line2);
    if (TEST_METHOD.matches(tree)) {
      System.out.println("方法检测匹配上了");
    }

    super.visitMethodInvocation(tree);
  }

  /**
   * 1.先找方法visitmethod:放进一个List,找到方法的行数区间，如果此方法的行数区间包含缺陷定义的行数，那么就执行下一步
   * 2.查找方法体visitMethodInvocation:看看方法体中是否调用的白名单方法
   */
}
