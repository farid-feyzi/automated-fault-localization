/*

COMPILE:
javac -cp .:/tmp/Lang37/target/:/tmp/Lang37/target/test-classes/:/tmp/Lang37/target/junit-4.10.jar:/tmp/Lang37/target/commons-lang-3.0-SNAPSHOT.jar InvokeTests.java

RUN:
java -cp .:/tmp/Lang37/target/:/tmp/Lang37/target/test-classes/:/tmp/Lang37/target/junit-4.10.jar:/tmp/Lang37/target/commons-lang-3.0-SNAPSHOT.jar InvokeTests <DEFECTS4J CHECKED OUT DIR> <COMMAND> <TEST CLASS NAME> <TEST METHOD NAME>

Explanation:
- <DEFECTS4J CHECKED OUT DIR> 	= /tmp/Lang37
- <COMMAND>						= runTestFile / runTestCase / getTestCases / getAssertLines
- <TEST CLASS NAME>				= org.apache.commons.lang3.ArrayUtilsAddTest
- <TEST METHOD NAME>			= "testJira567" (Needed only for command 
												"runTestCase")


*/

import java.io.*;
import java.util.*;
import org.junit.runner.*;
import org.junit.runner.notification.*;
import java.net.*;
import java.lang.reflect.*;

public class InvokeTests {

	public static void main(String[] args) {
		try {
			String directoryPath = null;
			if(args == null || args.length < 3) {
				System.out.println("Invalid paramters");
				System.exit(0);
			}
			directoryPath = args[0];
			File directory = new File(directoryPath);
			if(!directory.exists()) {
				System.out.println("Invalid project path");
				System.exit(0);
			}
			String command = args[1];

			List<String> validCommands = new ArrayList<String>();
			validCommands.add("runTestCase");
			validCommands.add("runTestFile");
			validCommands.add("getTestCases");
			validCommands.add("getAssertLines");

			String testClass = "";
			Class<?> c = null;
			String testCase = "";
			if(validCommands.contains(command)) {
				testClass = args[2];
				try {
					c = Class.forName(testClass);
				} catch(ClassNotFoundException ce) {
					System.out.println("Invalid test class name");
					System.exit(0);
				}

				if("runTestFile".equalsIgnoreCase(command)) {
					executeTestFile(c);
				} else if("runTestCase".equalsIgnoreCase(command)) {
					if(args.length != 4 || args[3].isEmpty()) {
						System.out.println("Invalid test case method name");
						System.exit(0);
					}
					testCase = args[3];
					executeSingleTestCase(c, testCase);
				} else if("getTestCases".equalsIgnoreCase(command)) {
					getTestCases(c, testClass);
				} else if("getAssertLines".equalsIgnoreCase(command)) {
					if(args.length != 4 || args[3].isEmpty()) {
						System.out.println("Invalid test case method name");
						System.exit(0);
					}
					testCase = args[3];
					getAssertLines(directoryPath, testClass, testCase);
				}
			} else {
				System.out.println("Invalid command. Supported commands: runTestFile, runTestCase, getTestCases");
				System.exit(0);
			}
		} catch (Exception e) {
			System.out.println("Error occurred");
			e.printStackTrace();
		}

	}

	public static void executeSingleTestCase(Class<?> c, String method) {
		Request request = Request.method(c, method);
		Result result = new JUnitCore().run(request);
		for(Failure failure : result.getFailures()) {
			System.out.println(failure.getTrace());
		}
		System.out.println(result.wasSuccessful());
	}

	public static void executeTestFile(Class c) {
		Result result = JUnitCore.runClasses(c);		
		for(Failure failure : result.getFailures()) {
			System.out.println(failure.getTrace());
		}
		System.out.println(result.wasSuccessful());
	}

	public static void getTestCases(Class c, String testClass) {
		Map<String, Set<String>> methods = new LinkedHashMap<String, Set<String>>();
	    for (Description test : Request.aClass(c).getRunner().getDescription().getChildren()) {
	      if (test.getMethodName() == null) {
	        for (Method m : c.getMethods()) {
	          if (m.isAnnotationPresent(org.junit.Test.class)
	              || Modifier.isPublic(m.getModifiers()) && m.getReturnType().equals(Void.TYPE)
	                  && m.getParameterTypes().length == 0 && m.getName().startsWith("test")) {
	            Set<String> ms = (methods.containsKey(testClass) ? methods.get(testClass)
	                : new LinkedHashSet<String>());
	            ms.add(m.getName() + test.getDisplayName());
	            methods.put(testClass, ms);
	          }
	        }
	      } else {
	        Set<String> ms =
	            (methods.containsKey(test.getClassName()) ? methods.get(test.getClassName())
	                : new LinkedHashSet<String>());
	        ms.add(test.getMethodName());
	        methods.put(test.getClassName(), ms);
	      }
		}
		
		for(String key : methods.keySet()) {
			Set<String> values = methods.get(key);
			for(String v : values) System.out.println(v);
		}
	}

	public static void getAssertLines(String dir, String testClass, String methodName) {
		String path = dir + "/src/test/";
		path += testClass.replaceAll("\\.", "/") + ".java";
		try {
			File file = new File(path);
			BufferedReader br = new BufferedReader(new FileReader(file));
			List<Integer> lines = new ArrayList<Integer>();
			int lineNo = 1;
			boolean methodStarted = false;
			String line;
			while((line = br.readLine()) != null) {
				if(line.contains(methodName + "(")) {
					methodStarted = true;
					lineNo++;
					continue;
				}
				if(methodStarted) {
					if(line.trim().startsWith("assert")) lines.add(lineNo);
					else if(line.trim().startsWith("public") || line.trim().startsWith("private") || line.trim().startsWith("protected")) {
						break;
					}
				}
				
				lineNo++;
			}
			for(int l : lines) System.out.println(l);
		} catch(Exception e) {
			System.out.println("Test file not found");
			System.exit(0);
		}
	}

}