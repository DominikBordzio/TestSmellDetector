package testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import testsmell.*;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class DeadFields extends AbstractSmell{

    private List<MethodDeclaration> testMethods;
    private List<FieldDeclaration> testFields;
    private List<String> strTestFields;
    private List<String> usedFields;

    public DeadFields() {
        testMethods = new ArrayList<>();
        testFields = new ArrayList();
        strTestFields = new ArrayList<>();
        usedFields = new ArrayList<>();
    }

    @Override
    public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException {
        DeadFields.ClassVisitor classVisitor;
        classVisitor = new DeadFields.ClassVisitor();
        classVisitor.visit(testFileCompilationUnit, null);

        strTestFields = strTestFields.stream().distinct().collect(Collectors.toList());

        for (MethodDeclaration method : testMethods) {
            classVisitor.visit(method, null);
        }

        usedFields = usedFields.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public String getSmellName() {
        return null;
    }

    @Override
    public boolean getHasSmell() {
        return false;
    }

    @Override
    public List<SmellyElement> getSmellyElements() {
        return null;
    }

    private class ClassVisitor extends VoidVisitorAdapter<Void> {

        private MethodDeclaration methodDeclaration = null;
        TestMethod testMethod;

        @Override
        public void visit(ClassOrInterfaceDeclaration declaration, Void arg) {
            NodeList<BodyDeclaration<?>> members = declaration.getMembers();
            for(int i = 0; i < members.size(); i ++) {
                if (members.get(i) instanceof MethodDeclaration) {
                    methodDeclaration = (MethodDeclaration) members.get(i);
                    if (Util.isValidTestMethod(methodDeclaration)) {
                        testMethods.add(methodDeclaration);
                    }
                }
                if(members.get(i) instanceof FieldDeclaration) {
                    testFields.add((FieldDeclaration) members.get(i));
                }
            }
        }
    }
}
