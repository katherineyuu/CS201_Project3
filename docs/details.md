# Starter Code and Using Git
**_You should have installed all software (Java, Git, VS Code) before completing this project._** You can find the [directions for installation here](https://coursework.cs.duke.edu/201fall23/resources-201/-/blob/main/installingSoftware.md) (including workarounds for submitting without Git if needed).

We'll be using Git and the installation of GitLab at [coursework.cs.duke.edu](https://coursework.cs.duke.edu). All code for classwork will be kept here. Git is software used for version control, and GitLab is an online repository to store code in the cloud using Git.

**[This document details the workflow](https://coursework.cs.duke.edu/201fall23/resources-201/-/blob/main/projectWorkflow.md) for downloading the starter code for the project, updating your code on coursework using Git, and ultimately submitting to Gradescope for autograding.** We recommend that you read and follow the directions carefully this first time working on a project! While coding, we recommend that you periodically (perhaps when completing a method or small section) push your changes as explained in below.

## DNA strands and the Starter Code

For the purposes of this project, DNA is represented as a sequence of characters, specifically `a`, `c`, `g`, and `t` for the four chemical bases of DNA. There can be a *lot* of these bases in a DNA sequence, so efficiency matters when dealing with DNA data computationally. This project includes a `data/` folder containing two data files: `ecoli.txt` and `ecoli_small.txt`, which represent the genetic information of ecoli - there are over 4.6 million bases in the full sequence in `ecoli.txt` and over 320 thousand in the `ecoli_small.txt` subsequence.

To get started you should read the comments in the `IDnaStrand` interface to understand what functionality implementations of that interface should provide with respect to manipulating DNA data. You will note that some methods in the interface have a `default` implementation provided, but most do not -- these are the methods you will be implementing. The `default` method `cutAndSplice` is the one that is benchmarked by the code provided in `DNABenchmark`, see [Part 1](#part-1-running-dnabenchmark-profiling-analysis) for a discussion of that method and its complexity.

Two relatively straightforward implementations of the `IDnaStrand` interface are provided in the starter code. `StringStrand` represents a DNA sequence as a simple String. `StringBuilderStrand` represents a DNA sequence as a  [`StringBuilder`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/StringBuilder.html). You can look at these two classese to see simple and correct (but not necessarily efficient) implementations of the functionality specified in the `IDnaStrand` interface.

## Complexity of cutAndSplice

The method `cutAndSplice` is not a mutator. It starts with a strand of DNA and creates a new strand by finding each and every occurrence of a restriction enzyme like `“gaattc”` and replacing this enzyme by a specified splicee -- another strand of DNA. If `dna` represents the strand `"cgatcctagatcgg"` then the call 

```java
dna.cutAndSplice("gat", "gggtttaaa")
```

would result in returning a new strand of DNA in which each occurrence of `"gat"` in `dna` is replaced by `"gggtttaaa"` -- as shown in the diagram below where the original strand is shown first, with the enzyme `"gat"` shaded in blue and the splicee `"gggtttaaa"` shaded in green. 

<div align="center">
  <img src="figures/splice.png">
</div>

The diagram illustrates how `cutAndSplice` works with both `StringStrand` and `StringBuilderStrand`. Each is a strand of 14 characters in which the restriction enzyme `"gat"` occurs twice, is replaced by `"gggtttaaa"`, resulting in creating and returning a new strand that contains 26 characters.

Note that if the original strand has size N, then the new strand has size N + b(S-E) where b is the number of breaks, or occurrences of the enzyme, S is the length of the splicee and E is the length of the enzyme. If we assume the splicee is large, as it will be when benchmarking, we can ignore E and this becomes approximately N + bS, the size of the recombinant new strand in terms. 

The runtime and memory complexity for `cutAndSplice` can be expressed as a function of N, b, and S. The complexity also depends on the implementation used for the `IDnaStrand` interface. Of particular importance for the runtime complexity is the efficiency of the `append` method, which you will note is called repeatedly by `cutAndSplice`. For the memory complexity, the question is how the implementation represents the resulting recombinant Strand.

## Running DNABenchmark

You select which implementation to use changing the value of the static instance variable `strandType` at the top of the class file. Note that the `StringStrand` class may take several seconds to run on `ecoli_small.txt`. `StringBuilderStrand` can scale to `ecoli.txt`, but you may not want to run `StringStrand` on the larger data set as it may take several minutes to run.

The main method benchmarks the average time (over several trials)  in milliseconds that it takes to run `cutAndSplice` for different values of N (the size of the original dna strand), b (the number of breaks / occurrences of the `enzyme`), and S (the size of the `splicee`). It also shows the size of the resulting recombininant new Strand, labeled as `recomb`, which is roughly equal to N + bS. 

First it performs several runs increasing S while holding the other values constant. Then it performs several runs increasing N and b while holding S constant. Example runs from an instructor's computer on `ecoli_small.txt` are shown below (note that your timings may differ but should show similar trends).

### StringStrand DNABenchmark Example Results

```
dna length = 320,160
cutting at enzyme gaattc
----------------------------------------------------------------------
Class             dna,N   splicee,S        recomb  time(ms)  breaks,b
----------------------------------------------------------------------
StringStra:     320,160      10,000       769,890        13        45
StringStra:     320,160      20,000     1,219,890        13        45
StringStra:     320,160      40,000     2,119,890        14        45
StringStra:     320,160      80,000     3,919,890        26        45
StringStra:     320,160     160,000     7,519,890        49        45
StringStra:     320,160     320,000    14,719,890       105        45
StringStra:     320,160     640,000    29,119,890       239        45
StringStra:     320,160   1,280,000    57,919,890       481        45
StringStra:     320,160      10,000       769,890         6        45
StringStra:     640,320      10,000     1,539,780        21        90
StringStra:   1,280,640      10,000     3,079,560        84       180
StringStra:   2,561,280      10,000     6,159,120       322       360
StringStra:   5,122,560      10,000    12,318,240     1,449       720
```

### StringBuilderStrand DNABenchmark Example Results

```
dna length = 320,160
cutting at enzyme gaattc
----------------------------------------------------------------------
Class             dna,N   splicee,S        recomb  time(ms)  breaks,b
----------------------------------------------------------------------
StringBuil:     320,160      10,000       769,890         1        45
StringBuil:     320,160      20,000     1,219,890         1        45
StringBuil:     320,160      40,000     2,119,890         1        45
StringBuil:     320,160      80,000     3,919,890         1        45
StringBuil:     320,160     160,000     7,519,890         2        45
StringBuil:     320,160     320,000    14,719,890         3        45
StringBuil:     320,160     640,000    29,119,890         6        45
StringBuil:     320,160   1,280,000    57,919,890         8        45
StringBuil:     320,160      10,000       769,890         1        45
StringBuil:     640,320      10,000     1,539,780         1        90
StringBuil:   1,280,640      10,000     3,079,560         3       180
StringBuil:   2,561,280      10,000     6,159,120         7       360
StringBuil:   5,122,560      10,000    12,318,240        15       720
```

