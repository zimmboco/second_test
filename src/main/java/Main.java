public class Main {
    public static void main(String[] args) {
        FirstTest firstTest = new FirstTest();
        firstTest.refactorNameAndSaveFormatXml("test.xml");
        firstTest.refactorNameXml("test.xml");

        SecondTest secondTest = new SecondTest();
        secondTest.calculate("file/");
    }
}
