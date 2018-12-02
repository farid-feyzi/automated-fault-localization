# FaultLocalizationResearch



For Mac & Linux

Step 1: Set up Defects4J
    
    Refer https://github.com/rjust/defects4j
    Clone the repository
    Follow documentation steps
      Error:Can't Locate DBI.pm 
      Resolution for Mac: perl -MCPAN -e 'install DBI'
                    Install Postgres if required.
      Resolution for Linux: sudo apt install libdbi-perl
        
Step 2: Clone the repo https://bitbucket.org/rjust/fault-localization-data/overview

Step 3: Download and install JDK 1.6 and JDK 1.8.

Step 4: Set evnironment variables
    
    The path of defects4j installed in step 1
      export D4J_HOME=/Users/{username}/Downloads/defects4j
    
    The path to this root directory of the cloned repo fault-localization-data and append 'gzoltar/gzoltar.jar'
      export GZOLTAR_JAR=/Users/{username}/Downloads/fault-localization-data/gzoltar/gzoltar.jar 
    
    Set JAVA_HOME to point to JDK1.6 Home if you have a different Java default version
      export JAVA_HOME=/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Home
      
    Add to PATH variable
      export PATH=$PATH:$D4J_HOME/framework/bin


Step 6:
    Replace the run_gzoltar.sh provided in this repository in fault-localization-data/gzoltar/gzoltar

Step 5: Test if set up works
    
    Test defects4j
    
        defects4j info -p Lang
        
    Test Gzoltar 
    
        `bash run_gzoltar.sh Lang 37 . developer`
        
Step 7: Install sloccount
    
    For Mac: brew install sloccount
    For Mac: export SLOC_HOME=/usr/local/bin/sloccount
    For Ubuntu: sudo apt-get install sloccount
    For Ubuntu: export SLOC_HOME=/usr/bin/sloccount
    
Step 8: Run the script to get the buggy code
    
    bash get_fixed_lines.sh Lang 37 .
    
    This creates a file `Lang-37.fixed.lines` which contains the line fixed in the human patch for the corresponding bug.
    This can be used to evaluate the suspiciousness score generated by the Fault localization technique.

To manually run junit tests in the defects4j projects

Step 1: checkout defects4j project(example: Lang)
    
    defects4j checkout -p Lang -v 37b -w /tmp/Lang37
    
Step 2: Download JUNIT Jar

    Download the junit jar from https://github.com/downloads/junit-team/junit/junit-4.10.jar

Step 3: Downgrade Maven to mvn 3.2 so as to run with Java 1.6

    Check maven version by running.
        mvn --version
    If this is 3.2 or below then skip to Step 4.
    If not follow these steps to downgrade maven
        brew install maven@3.2
        brew unline maven
        brew link --force --overwrite maven@3.2
        
Step 4: Compile Lang Project
    
    Go to the checked out Lang project folder in two session on Terminal
    Session 1:
        export JAVA_HOME=/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Home
        mvn compile (This will fail)
    Session 2:
        mvn compile
        rm -rf target/*
    Session 1:
        mvn compile (This should succeed)

Step 5: Run all of the Junit tests in Lang Project using maven
    
    Session 1:
        mvn -Dmaven.test.failure.ignore=true install (This will fail)
    Session 2:
        mvn -Dmaven.test.failure.ignore=true install
        rm -rf target/commons-lang-3.0-SNAPSHOT.jar
    Session 1:
        mvn -Dmaven.test.failure.ignore=true install (This should succeed)
        mvn test
        
Step 6: Run one of the Junit tests in Lang Project from command line without maven

    cd target
    cp {Path to junit jar}/junit-4.10.jar .
    java -cp .:/tmp/Lang37/target/test-classes/:junit-4.10.jar:commons-lang-3.0-SNAPSHOT.jar org.junit.runner.JUnitCore org.apache.commons.lang3.ArrayUtilsAddTest

Step 7a: Setting up javaslicer
    Clone the repository for javaslicer (https://github.com/hammacher/javaslicer)
    cd javaslicer
    ./assemble.sh
    (Note: There might be failures when you run this the first time. Run ./assemble.sh to fix it.)

Step 7b: Run one of the junit test classes with tracer as a javaagent attached.
    
    Session 2:
        java -cp .:/tmp/Lang37/target/test-classes/:junit-4.10.jar:commons-lang-3.0-SNAPSHOT.jar -javaagent:/Users/jithinjohn/Downloads/CSC591/javaslicer/assembly/tracer.jar=tracefile:test.trace org.junit.runner.JUnitCore org.apache.commons.lang3.ArrayUtilsAddTest

Step 8: Run slicer to produce slicing results
    
    Session 2:
        java -Xmx2g -jar /Users/jithinjohn/Downloads/CSC591/javaslicer/assembly/slicer.jar -p test.trace org.apache.commons.lang3.ArrayUtils.addAll:2962:* > output.txt

### Automation

Change directory to automation/src/

COMPILE:

javac -cp .:/tmp/Lang37/target/:/tmp/Lang37/target/test-classes/:/tmp/Lang37/target/junit-4.10.jar:/tmp/Lang37/target/commons-lang-3.0-SNAPSHOT.jar InvokeTests.java
        
RUN:

1) Running a test file

java -cp .:/tmp/Lang37/target/:/tmp/Lang37/target/test-classes/:/tmp/Lang37/target/junit-4.10.jar:/tmp/Lang37/target/commons-lang-3.0-SNAPSHOT.jar InvokeTests /tmp/Lang37 runTestFile org.apache.commons.lang3.ArrayUtilsAddTest
        
2) Running a test specific test case

java -cp .:/tmp/Lang37/target/:/tmp/Lang37/target/test-classes/:/tmp/Lang37/target/junit-4.10.jar:/tmp/Lang37/target/commons-lang-3.0-SNAPSHOT.jar InvokeTests /tmp/Lang37 runTestCase org.apache.commons.lang3.ArrayUtilsAddTest testJira567
    
3) Getting test cases of a test file

java -cp .:/tmp/Lang37/target/:/tmp/Lang37/target/test-classes/:/tmp/Lang37/target/junit-4.10.jar:/tmp/Lang37/target/commons-lang-3.0-SNAPSHOT.jar InvokeTests /tmp/Lang37 getTestCases org.apache.commons.lang3.ArrayUtilsAddTest

4) Getting line numbers of assert statements in a test case

java -cp .:/tmp/Lang37/target/:/tmp/Lang37/target/test-classes/:/tmp/Lang37/target/junit-4.10.jar:/tmp/Lang37/target/commons-lang-3.0-SNAPSHOT.jar InvokeTests /tmp/Lang37 getAssertLines org.apache.commons.lang3.ArrayUtilsAddTest testJira567

# Fault-localization-data repository

This repository contains data files, data-collection scripts, and data-analysis
scripts of the "Evaluating and Improving Fault Localization Techniques" project.
Before exploring this repository, please read the [technical
report](http://homes.cs.washington.edu/~mernst/pubs/fault-localization-tr160803.pdf) that describes the results.

Overview
========
The experiments evaluate various fault localization techniques on artificial faults and on real faults.

At a high level, here's how it all works:

- The real and artificial faults come from the [Defects4J Project](https://github.com/speezepearson/defects4j).
- For each D4J fault, the scripts in `d4j_integration/` determine which lines are faulty. The resultant files are "buggy-lines" files, and live in `analysis/pipeline-scripts/buggy-lines/`.
- Many fault localization techniques require coverage information.  We use [GZoltar](http://www.gzoltar.com/) to gather coverage information.  The resultant files are called "matrix" and "spectra".
- Mutation-based fault localization (MBFL) techniques require mutation analysis.  Our Killmap project (which lives in `killmap/`) does mutation analysis on all faults. The resultant files are called "killmaps," and specify how each test behaves on each mutant. (Each killmap also has an associated "mutants-log" file, which describes all the mutants that were analyzed.)
- Our scripts enable you to compute all the mutation and coverage information, but doing so takes a great deal of computation. The resulting mutation/coverage information is available at [http://fault-localization.cs.washington.edu](http://fault-localization.cs.washington.edu).
- The "scoring pipeline" (which lives in `analysis/pipeline-scripts/`) determines how well each FL technique does on each fault -- that is, where the real buggy lines appear in the FL technique's ranking of the line of the program.  The results appear in `data/`.



How-To
======

Before doing anything else, run `./setup.sh`. This:

- clones [the appropriate Defects4J fork](https://github.com/speezepearson/defects4j) (unless you've already exported a `D4J_HOME` directory);
- updates your `.bashrc` to export some environment variables:
    + `D4J_HOME` and `DEFECTS4J_HOME`, pointing to the new `defects4j` repository, if it needed
    + `FL_DATA_HOME`, pointing here
    + `KILLMAP_HOME`, pointing at `./killmap/`
    + `GZOLTAR_JAR`, pointing to `./gzoltar/gzoltar.jar`

### How to score techniques
The workflow to score a set of FL techniques on a given fault looks like [this](.README-dataflow.png):

> ![](.README-dataflow.png)

* Various pieces of fault information were generated by the tools in `./d4j_integration/` and then checked in. You don't need to generate them yourself, but if you want to, see the `README.md` in that directory.

* To run GZoltar, use `gzoltar/run_gzoltar.sh`.

    > Example invocation: `bash run_gzoltar.sh Lang 37 . developer`
    >
    > Creates the files `./matrix` and `./spectra`.

* To run Killmap, use `killmap/scripts/generate-matrix`.

    > Example invocation:
    >
    >     killmap/scripts/generate-matrix \
    >       Lang 37 \
    >       /tmp/Lang-37 \
    >       Lang-37.mutants.log \
    >       | gzip > Lang-37.killmap.csv.gz
    >
    > Creates the files `Lang-37.killmap.csv.gz` and `Lang-37.mutants.log`.

* To run the scoring pipeline, use `analysis/pipeline-scripts/do-full-analysis`.

    > Example invocation:
    >
    >     analysis/pipeline-scripts/do-full-analysis \
    >       Lang 37 'developer' \
    >       ./matrix ./spectra \
    >       Lang-37.killmap.csv.gz Lang-37.mutants.log \
    >       /tmp/Lang-37-scoring \
    >       Lang-37.scores.csv`
    >
    > Creates the file `Lang-37.scores.csv`.

For more details on any of these scripts, see the `README.md` in the script's directory.

If you want to skip running GZoltar and Killmap (which can be very computationally expensive), you can download the resulting files from <http://fault-localization.cs.washington.edu>.


Contents
========

* `analysis/`: Tools for analyzing the output of coverage/mutation analyses.

* `aws/`: Scripts for computing killmaps on AWS.

* `cluster_scripts/`: Scripts for computing killmaps on a Sun Grid cluster.

* `d4j_integration/`: Scripts that build upon or extend Defects4J to populate or query its database.

* `data/`: Data files for the final results and corresponding support scripts.

* `gzoltar/`: Scripts for running the [GZoltar](http://www.gzoltar.com/) tool to collect line coverage information.

* `killmap/`: Mutation-analysis tool whose output is used for the MBFL techniques we study.

* `stats/`: R scripts that crunch the data to produce numbers for the paper.

* `utils/`: Utility programs and libraries for running/analyzing tests and parsing data files.
=======
=======
