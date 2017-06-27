package testsmell.smell;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import testsmell.ISmell;
import testsmell.MethodSmell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThreadSleep implements ITestSmell {

    List<ISmell> smellList;

    @Override
    public List<ISmell> runAnalysis(CompilationUnit cu) {
        smellList = new ArrayList<>();

        ThreadSleep.ClassVisitor classVisitor = new ThreadSleep.ClassVisitor();
        classVisitor.visit(cu, null);

        return smellList;
    }

    private class ClassVisitor extends VoidVisitorAdapter<Void> {
        private MethodDeclaration currentMethod = null;
        private int sleepCount =0;
        ISmell methodSmell;
        Map<String, String> map;

        // examine all methods in the test class
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            //only analyze methods that either have a @test annotation (Junit 4) or the method name starts with 'test'
            if(n.getAnnotationByName("Test").isPresent() || n.getNameAsString().toLowerCase().startsWith("test") ){
                currentMethod = n;
                methodSmell = new MethodSmell(currentMethod.getNameAsString());
                super.visit(n, arg);

                methodSmell.setHasSmell(sleepCount >= 1);

                map = new HashMap<>();
                map.put("ThreadSleepCount", String.valueOf(sleepCount));
                methodSmell.setSmellData(map);

                smellList.add(methodSmell);

                //reset values for next method
                currentMethod = null;
                sleepCount = 0;
            }
        }

        // examine the methods being called within the test method
        @Override
        public void visit(MethodCallExpr n, Void arg) {
            super.visit(n, arg);
            if (currentMethod != null){
                // if the name of a method being called is 'sleep'
                if(n.getNameAsString().equals("sleep") ){
                    //check the scope of the method
                    if((n.getScope().isPresent() && n.getScope().get() instanceof NameExpr)){
                        //proceed only if the scope is "Thread"
                        if((((NameExpr) n.getScope().get()).getNameAsString().equals("Thread"))){
                            sleepCount++;
                        }
                    }

                }
            }
        }

    }
}
