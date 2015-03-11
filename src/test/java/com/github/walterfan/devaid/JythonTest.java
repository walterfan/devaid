package com.github.walterfan.devaid;


import org.python.core.PyException;
import org.python.core.PyInteger;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;


public class JythonTest {

    public static void main(String[] args) throws PyException {
        PythonInterpreter interp = new PythonInterpreter();
        interp.exec("import sys");
        interp.exec("print sys");
        interp.set("a", new PyInteger(42));
        interp.exec("print a");
        interp.exec("x = 2 + 2");
        PyObject x = interp.get("x");
        System.out.println("x: " + x);
    }
}
