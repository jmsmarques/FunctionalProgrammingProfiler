package ist.meic.pa.FunctionalProfiler;

class WithFunctionalProfiler {

    public static void main(String[] args) {
        printResult();
    }

    private static void printResult() {
        System.out.println(String.format("Total reads: %s Total writes: %s", 6, 1));
        System.out.println(String.format("class FunctionalCounter -> reads: %s writes: %s", 3, 0));
        System.out.println(String.format("class ImperativeCounter -> reads: %s writes: %s", 3, 1));
    }
}