import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.Scanner;
public class PerformanceAnalyzerForJavaCollections {
    public static void main(String[] args) {

        if (args.length != 0 && args.length != 5) {
            System.out.println("Please enter only 5 values,without repetitions\nIn whatever position you want to,can be like:\njava PerformanceAnalyzerForJavaCollections ArrayList Integer 750 ByIndex Console\nExiting program...");
            System.exit(1);
        }
        //Calling TestRunner(method that does all work)
        TestRunner(args);
    }


    //Class that validate all inputs from CLI raw input/or Scanner if args are empty(args[0]),and shows all info
    public static void TestRunner(String[] args){
        //initializing enums and values
        CollectionType collectionType = null;
        DataType dataType = null;
        int numOfGens = 0;
        TestType testType = null;
        PresentationFormat presentationFormat = null;

        try {
            if (args.length == 0) {
                collectionType = chooseEnum(CollectionType.values(), "Please enter which CollectionType you want to perform on:");
                dataType = chooseEnum(DataType.values(), "Please enter which DataType you want to perform on:");
                testType = chooseEnum(TestType.values(), "Please enter which TestType you want to perform on:");
                numOfGens = isRightDigit();
                presentationFormat = chooseEnum(PresentationFormat.values(), "Please enter how you wanna get output:");
            }else if (args.length == 5) {
                //if(args[i] == one of enum types),we assign this value to our enum
                for (int i = 0; i < args.length; i++) {
                    if (isValidEnum(CollectionType.values(), args[i])) {
                        collectionType = collectionType.valueOf(args[i]);
                    }
                    if (isValidEnum(DataType.values(), args[i])) {
                        dataType = dataType.valueOf(args[i]);
                    }
                    if (isDigit(args[i])) {
                        numOfGens = Integer.parseInt(args[i]);
                    }
                    if (isValidEnum(TestType.values(), args[i])) {
                        testType = testType.valueOf(args[i]);
                    }
                    if (isValidEnum(PresentationFormat.values(), args[i])) {
                        presentationFormat = presentationFormat.valueOf(args[i]);
                    }

                    //there could be situation where user forgot to put all values to CLI or put incorrect ones,so we need to stop the program
                    if (collectionType == null || dataType == null || testType == null || presentationFormat == null || numOfGens <= 0) {
                        throw new IllegalArgumentException("Some arguments are missing or incorrect,your input must look like this for instance:\njava PerformanceAnalyzerForJavaCollections ArrayList Integer 750 ByIndex Console");
                    }
                }
            }

            //stopping program if user tried HashSet or TreeSet with Index test
            if ((collectionType == CollectionType.valueOf("HashSet") || collectionType == CollectionType.valueOf("TreeSet")) && testType == TestType.valueOf("ByIndex")) {
                System.out.println("Index-based access is not supported for Sets!!!\nTry again!\nExiting...");
                System.exit(1);
            }

            System.out.println("Test " + testType.name() + " started.");

            //PROCESSING TESTS AND RETURNING ArrayList OF TIME
            TestMaker testMaker = new TestMaker();
            ArrayList<?> testRes = testMaker.testForColl(collectionType,testType,dataType,numOfGens);

            //CALLING TestResult FOR OUTPUTTING THE PROGRAM
            TestResult.InfoPrinter(testRes,testType,presentationFormat);

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Please enter a valid number of arguments or check validity of arguments and try again\nExiting program...");
            System.exit(1);
        }
    }

    //Generic class for validating input from Scanner
    public static <T extends Enum<T>> T chooseEnum(T[] values, String prompt) {
        Scanner sc = new Scanner(System.in);
        System.out.println(prompt);
        for (int i = 0; i < values.length; i++) {
            System.out.println((i + 1) + "." + values[i].name());
        }
        System.out.print("Type number: ");
        int choice = sc.nextInt();
        if (choice < 1 || choice > values.length) {
            System.out.println("Invalid choice! Exiting...");
            System.exit(1);
        }
        return values[choice - 1];
    }

    //Class for validating is the num of gen from Scanner
    public static int isRightDigit() {
        int[] genNums = {100,500,1000,10000};
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter on how many nums of gens you want to perform on:");
        for (int i = 0; i < genNums.length+1; i++) {
            if (i !=genNums.length) {
                System.out.println((i + 1) + "." + genNums[i]);
            }else{
                System.out.println((i + 1) + "." + "Your own number");
            }
        }
        System.out.print("Type number: ");
        int choice = sc.nextInt();
        if (choice < 1 || choice > genNums.length + 1) {
            System.out.println("Invalid choice! Exiting...");
            System.exit(1);
        }else if(choice == genNums.length + 1){
            System.out.print("Your own number:");
             choice = sc.nextInt();
        }
        return choice;
    }


    //Generic class for validating input from raw CLI(args)
    public static <T extends Enum<T>> boolean isValidEnum(T[] values, String value) {
        //going through all values of enum and looking for match one
        for (T t : values) {
            //if found than return true
            if (t.name().equals(value)){
                return true;
            }
        }
        //didn't? -> false
        return false;
    }

    //Class for validating is the args[i] is a digit
    public static boolean isDigit(String s) {
        try {
            int num = Integer.parseInt(s);
            //safe as after exception execution stops
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


}


//interfaces

//Interface for class StorageMaker(creating collections)
interface StorageInterface<T>{
    Collection<T> genCollection(CollectionType type);
}

//Interface for class TestMaker(doing test and measuring time)
interface TestInterface<T>{
    ArrayList<Double> testForColl(CollectionType collType, TestType type,DataType dataType,int size);
}

//Interface for class ContentInputer(streams of given datatype)
//we need 2 methods as we need many generations for collections and only one generation for test isInColl & IRFrequency(only second *commented* implementation of Remove test))
interface ContentInterface<T>{
    void contentCollGen(Collection coll, DataType type,int size);//for generating values for collections
    Object contentGen(DataType type);//for generating ONLY 1 value for TEST "IsInColl"
}

//end of interfaces



//classes

//Class for generating collections
class StorageMaker<T> implements StorageInterface<T>{

    @Override
    public Collection<T> genCollection(CollectionType type) {
        switch (type) {
            case ArrayList -> {return new ArrayList<T>();}
            case LinkedList -> {return new LinkedList<T>();}
            case HashSet -> {return new HashSet<T>();}
            case TreeSet -> {return new TreeSet<T>();}
            //I needed default as compiler doesn't see that these are all options
            default -> {throw new IllegalArgumentException();}
        }

    }
}

//Class for Tests on collections
class TestMaker implements TestInterface{
    public static Random rand = new Random();
    public static ContentInputer cI = new ContentInputer();
    public static StorageMaker storageMaker = new StorageMaker();
    @Override
    public ArrayList<Double> testForColl(CollectionType collType, TestType type,DataType dataType,int size) {
        ArrayList<Double> timeSpent = new ArrayList<>();
        Collection coll = storageMaker.genCollection(collType);
        long start = 0;
        long end = 0;
        long durationNs = 0;

        switch (type) {
            case ByIndex -> {

                List list = (List) coll;//casting collection to list for accessing indexed values


                cI.contentCollGen(coll, dataType,size);//generating values


                int splitColl = coll.size()/2;//picking value

                start = System.nanoTime();//operation start time

                var a = list.get(splitColl);

                end = System.nanoTime();//operation done time

                durationNs = end - start;//duration of operation

                double durationSec = durationNs / 1_000_000_000.0; //returning in seconds,as 1 sec == 1_000_000_000(1 billion)nanosec

                timeSpent.add(durationSec);//returning time
            }

            //Here I am not sure if I did it right,but I tried...
            case IRFrequency -> {
                double startInput = System.nanoTime();//insertion start time

                cI.contentCollGen(coll, dataType,size);//inputting values

                double endInput = System.nanoTime();//insertion end time

                Object[] collArr = coll.toArray();
                double startRem = System.nanoTime();//removing start time
                while (coll.size() != 0){
                    for (int i = 0; i < collArr.length; i++){
                        if(coll.contains(collArr[i])){
                        coll.remove(collArr[i]);
                        }
                    }
                }
                double endRem = System.nanoTime();//removing done time


                double inputDurationNs = endInput - startInput;//duration of inputting

                double removeDurationNs =endRem - startRem;//duration of removal

                double inputDurationSec = inputDurationNs / 1_000_000_000.0; //returning in seconds,as 1 sec == 1_000_000_000(1 billion)nanosec
                double remDurationSec = removeDurationNs / 1_000_000_000.0; //returning in seconds,as 1 sec == 1_000_000_000(1 billion)nanosec

                timeSpent.add(inputDurationSec);
                timeSpent.add(remDurationSec);



                //here is one of possible implementation of Removing test,I was not sure If I did it right,so I made more safe one,that is above
                /*      while(coll.size() != 0){
                    var b = cI.contentGen(dataType);
                    if(coll.contains(b)){
                        coll.remove(b);
                    }
                }*/
            }
            case Searching -> {
                cI.contentCollGen(coll, dataType,size);//inputting values

                Object[] collArr = coll.toArray();
                Object target = collArr[rand.nextInt(collArr.length)];


                start = System.nanoTime();//operation start time


                coll.contains(target);


                end = System.nanoTime();//operation done time

                durationNs = end - start;//duration of operation

                double durationSec = durationNs / 1_000_000_000.0; //returning in seconds,as 1 sec == 1_000_000_000(1 billion)nanosec
                timeSpent.add(durationSec);//returning time
            }
            case IsInColl -> {

                var oInColl = cI.contentGen(dataType);
                cI.contentCollGen(coll, dataType,size);//inputting values

                start = System.nanoTime();//operation start time

                coll.contains(oInColl);

                end = System.nanoTime();//operation done time
                durationNs = end - start;//duration of operation
                double durationSec = durationNs / 1_000_000_000.0; //returning in seconds,as 1 sec == 1_000_000_000(1 billion)nanosec

                timeSpent.add(durationSec);
            }
        }

        return timeSpent;
    }
}

//Class for inputting contents
class ContentInputer implements ContentInterface{
    public static Random rand = new Random();

    @Override
    public void contentCollGen(Collection coll, DataType type,int size){
        switch (type){
            case Integer -> {
                IntStream.generate(rand::nextInt)
                        .limit(size)
                        .forEach(coll::add);
            }
            case Double -> {
                DoubleStream.generate(rand::nextDouble)
                        .limit(size)
                        .forEach(coll::add);
            }
            case Person -> {
                Stream.generate(Person::new)
                        .limit(size)
                        .forEach(coll::add);
            }
            case MyColor -> {
                Stream.generate(MyColor::new)
                        .limit(size)
                        .forEach(coll::add);
            }
            case Subject -> {
                Stream.generate(Subject::new)
                        .limit(size)
                        .forEach(coll::add);
            }
            case Game -> {
                Stream.generate(Game::new)
                        .limit(size)
                        .forEach(coll::add);
            }
        }
    }
    @Override
    public Object contentGen(DataType type) {
        switch (type) {
            case Integer -> {
                return rand.nextInt();
            }
            case Double -> {
                return rand.nextDouble();
            }
            case Person -> {
                return new Person();
            }
            case MyColor -> {
                return new MyColor();
            }
            case Subject -> {
                return new Subject();
            }
            case Game -> {
                return new Game();
            }
            default -> {return null;}
        }
    }
}

//Class with static function that dealing with output(CLI/CSV)
class TestResult{

    public static void InfoPrinter(ArrayList<?> list, TestType testType, PresentationFormat presentationFormat) {
        StringBuilder sb = new StringBuilder();
        if (presentationFormat == PresentationFormat.Console) {
            switch (testType) {
                case ByIndex -> {
                    sb.append(String.format("\nIndex-based access took:%.9f sec\n", list.get(0)));
                }
                case IRFrequency -> {
                    sb.append((String.format("\nInsertion took:%.9f sec\n", list.get(0))));
                    sb.append((String.format("Removing took:%.9f sec", list.get(1))));
                }
                case Searching -> {
                    sb.append(String.format("\nSearch for existing element took: %.9f sec", list.get(0)));
                }
                case IsInColl -> {
                    sb.append(String.format("\nSearch for random value took: %.9f sec\n", list.get(0)));
                }
            }
            System.out.println(sb.toString());

        } else {
            String baseName = "test_result.csv";
            File fileOutput = new File(baseName);
            int version = 1;

            while (fileOutput.exists()) {
                fileOutput = new File("test_result_" + version + ".csv");
                version++;
            }


            switch (testType) {
                case ByIndex -> {
                    sb.append(String.format("Index-based access\n%.9f sec", list.get(0)));
                }
                case IRFrequency -> {
                    sb.append("Insertion;Removing\n");
                    sb.append(String.format("%.9f sec;", list.get(0)));
                    sb.append(String.format("%.9f sec\n", list.get(1)));
                }
                case Searching -> {
                    sb.append(String.format("Search for existing element\n%.9f sec", list.get(0)));
                }
                case IsInColl -> {
                    sb.append(String.format("Search for random value\n%.9f sec\n", list.get(0)));
                }
            }


            try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileOutput))) {
                bw.write(sb.toString());

                System.out.println("Results saved to: " + fileOutput.getName()+"\nTo the address: " + fileOutput.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Error writing CSV: " + e.getMessage());
            }
        }
    }
}

//this class allows us to create random Strings up to 100 chars(we use them in classes(Person,MyColor,etc.))
class RandStr{

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ- abcdefghijklmnopqrstuvwxyz";
    private static Random rand = new Random();

    public static String randomString() {
        Random rand = new Random();
        int length = rand.nextInt(100);
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = rand.nextInt(CHARS.length());
            sb.append(CHARS.charAt(index));
        }
        return sb.toString();
    }
}

//end of classes

//enums

//Data Type of for class ContentInputer
enum DataType{Integer,Double,Person,MyColor,Subject,Game}
//Collection type for class StorageMaker
enum CollectionType{ArrayList,LinkedList,HashSet,TreeSet}
//TestType type for class TestMaker
enum TestType{ByIndex, IRFrequency,Searching,IsInColl}
//Output options
enum PresentationFormat{Console,CSV}
//end of enums


//Content classes
//Strings of classes are filled with RandStr class, so just "random" strings :)

//fName - first name, lName - last name, yBirth - year of birth. Total [3] values
class Person implements Comparable<Person>{
    private String fName;
    private String lName;
    private int yBirth;
    private static RandStr rand = new RandStr();
    private static Random r = new Random();

    public Person(){

        this.fName = rand.randomString();
        this.lName = rand.randomString();
        this.yBirth = r.nextInt(2026);
    }
    @Override
    public String toString() {
        return "Name:" + fName + "\nLast Name:" + lName+"\nYear of Birth:" + yBirth;
    }
    @Override
    public boolean equals(Object ob) {
        if (ob == null || getClass() != ob.getClass()) return false;
        Person p = (Person) ob;
        return fName.equals(p.fName) && lName.equals(p.lName) && yBirth == p.yBirth;
    }
    @Override
    public int hashCode() {
        return 17*fName.hashCode() +lName.hashCode() + yBirth;
    }
    @Override
    public int compareTo(Person p) {
        int lastNameCompare = this.lName.compareTo(p.lName);
        if (lastNameCompare != 0) return lastNameCompare;

        int firstNameCompare = this.fName.compareTo(p.fName);
        if (firstNameCompare != 0) return firstNameCompare;

        return Integer.compare(this.yBirth, p.yBirth);
    }



}

//r - red, g - green, b - blue , sum = r+g+b . Total [4] values
class MyColor implements Comparable<MyColor>{
    private int r;
    private int g;
    private int b;
    private int sum;
    private static Random rand = new Random();
    public MyColor(){
        this.r = rand.nextInt(256);
        this.g = rand.nextInt(256);
        this.b = rand.nextInt(256);
        this.sum = r+g+b;
    }
    @Override
    public String toString() {
        return "R=" + r + "\nG=" + g + "\nB=" + b +"\nSUM=" + sum;
    }

    @Override
    public boolean equals(Object ob) {
        if (ob == null || getClass() != ob.getClass()) return false;
        MyColor mC = (MyColor) ob;
        return r == mC.r && g == mC.g && b == mC.b && sum == mC.sum;
    }

    @Override
    public int hashCode() {
        Integer hashSum = this.sum;
        return hashSum.hashCode();
    }

    @Override
    public int compareTo(MyColor mC) {
        int rComp = Integer.compare(this.r, mC.r);
        if (rComp != 0) return rComp;

        int gComp = Integer.compare(this.g, mC.g);
        if (gComp != 0) return gComp;

        int bComp = Integer.compare(this.b, mC.b);
        if (bComp != 0) return bComp;

        return Integer.compare(this.sum, mC.sum);
    }


}

//sName - name of subject, id - ... . Total [2] values
class Subject implements Comparable<Subject>{
    private String sName;
    private int id;
    private static RandStr rand = new RandStr();
    private static Random r = new Random();
    public Subject() {
        this.sName = rand.randomString();
        this.id = r.nextInt(1000000);

    }
    @Override
    public String toString() {
        return "Name: " + sName + "\nID" + id;
    }
    @Override
    public boolean equals(Object ob) {
        if (ob == null || getClass() != ob.getClass()) return false;
        Subject sb = (Subject) ob;
        return sName.equals(sb.sName) && id == sb.id;
    }
    @Override
    public int hashCode() {
        return 17*sName.hashCode() + id;
    }
    @Override
    public int compareTo(Subject sb) {
        int nameComp = this.sName.compareTo(sb.sName);
        if (nameComp != 0) return nameComp;

        return Integer.compare(this.id, sb.id);
    }

}

//gameName - ... , genre - ... . Total [2] values
class Game implements Comparable<Game>{
    private String gameName;
    private String genre;
    private static RandStr rand = new RandStr();

    public Game() {
        this.gameName = rand.randomString();
        this.genre = rand.randomString();
    }

    @Override
    public String toString() {
        return "Game Name: "+gameName+"\nGanre:"+genre;
    }

    @Override
    public boolean equals(Object ob) {
        if (ob == null || getClass() != ob.getClass()) return false;
        Game gm = (Game) ob;
        return gameName.equals(gm.gameName) && genre.equals(gm.genre);
    }

    @Override
    public int hashCode() {
        return 17*gameName.hashCode() + genre.hashCode();
    }

    @Override
    public int compareTo(Game gm) {
        int nameComp = this.gameName.compareTo(gm.gameName);
        if (nameComp != 0) return nameComp;

        return this.genre.compareTo(gm.genre);
    }
}

//end of content classes



