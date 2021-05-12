package testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import testsmell.AbstractSmell;
import testsmell.SmellyElement;
import testsmell.TestMethod;
import testsmell.Util;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DeadFields extends AbstractSmell{

    private List<MethodDeclaration> testMethods;
    private List<FieldDeclaration> testFields;
    private List<String> testFieldsStr;
    private List<String> usedFields;
    private boolean smelly = false;

    public DeadFields() {
        testMethods = new ArrayList<>();
        testFields = new ArrayList();
        testFieldsStr = new ArrayList<>();
        usedFields = new ArrayList<>();
    }

    @Override
    public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit, String testFileName, String productionFileName) throws FileNotFoundException {
        DeadFields.ClassVisitor classVisitor;
        classVisitor = new DeadFields.ClassVisitor();
        classVisitor.visit(testFileCompilationUnit, null);

        for (MethodDeclaration method : testMethods) {
            classVisitor.visit(method, null);
        }

        usedFields = usedFields.stream().distinct().collect(Collectors.toList());
        testFieldsStr = testFieldsStr.stream().distinct().collect(Collectors.toList());
        testFieldsStr.removeAll(usedFields);
        smelly = testFieldsStr.size() > 0;
        System.out.println(smelly);
    }

    @Override
    public String getSmellName() {
        return "Dead Fields";
    }

    @Override
    public boolean getHasSmell() {
        return smelly;
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
                    testFieldsStr.add(((FieldDeclaration) members.get(i)).getVariable(0).getNameAsString());
                    testFields.add((FieldDeclaration) members.get(i));
                }
            }
        }

        @Override
        public void visit(MethodDeclaration n, Void arg) {
            if (Util.isValidTestMethod(n)) {
                super.visit(n, arg);
                testMethod = new TestMethod(n.getNameAsString());
                getAllChildNodes(n);
            }
        }

        //Get all NameExpr inside of testMethod
        public void getAllChildNodes(Node n){
            List<Node> children = n.getChildNodes();
            children.stream().forEach(x -> {
                if(x instanceof NameExpr) {
                    usedFields.add(((NameExpr) x).getNameAsString());
                }
                else getAllChildNodes(x);
            });
        }
    }
}
