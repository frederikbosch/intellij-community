@p.I package p;

public @interface I {
public int x() ;
}
@p.I public class A extends groovy.lang.GroovyObjectSupport implements groovy.lang.GroovyObject {
@p.I public A() {
}
}
public class useAnno extends groovy.lang.Script {
public static void main(java.lang.String[] args) {
new p.useAnno(new groovy.lang.Binding(args)).run();
}

public java.lang.Object run() {
@p.I public java.lang.Integer var = 3;
return null;

}

@p.I public void foo(java.lang.Object x) {
@p.I java.lang.Object a;
}

public useAnno(groovy.lang.Binding binding) {
super(binding);
}
public useAnno() {
super();
}
}
