package ed.inf.adbs.lightdb;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestGivenExample {

    public boolean compare(String num) {
        String[] args = {"/samples/db", "/samples/input/query" + num + ".sql", "/samples/output/query" + num + ".csv", ""};
        LightDB.main(args);
        String expected = "";
        try {
            File file = new File("samples/expected_output/query" + num + ".csv");
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                expected = expected + scanner.next() + "\n";
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail();
        }

        String my = "";
        try {
            File file = new File( "samples/output/query" + num + ".csv");
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                my = my + scanner.next() + "\n";
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail();
        }

        for (int i = 0; i < my.length(); i++) {
            if (i == expected.length() || my.charAt(i) != expected.charAt(i)) {
                return false;
            }
        }

        File outputFile = new File("samples/output/query" + num + ".csv");
        if (outputFile.delete()) {
            System.out.println("Deleted the file: " + outputFile.getName());
        } else {
            System.out.println("Failed to delete the file.");
            fail();
        }

        return true;
    }

    @Test
    public void testMain1() {
        assertTrue(compare("1"));
    }

    @Test
    public void testMain2() {
        assertTrue(compare("2"));
    }

    @Test
    public void testMain3() {
        assertTrue(compare("3"));
    }

    @Test
    public void testMain4() {
        assertTrue(compare("4"));
    }

    @Test
    public void testMain5() {
        assertTrue(compare("5"));
    }

    @Test
    public void testMain6() {
        assertTrue(compare("6"));
    }

    @Test
    public void testMain7() {
        assertTrue(compare("7"));
    }

    @Test
    public void testMain8() {
        assertTrue(compare("8"));
    }

    @Test
    public void testMain9() {
        assertTrue(compare("9"));
    }

    @Test
    public void testMain10() {
        assertTrue(compare("10"));
    }

    @Test
    public void testMain11() {
        assertTrue(compare("11"));
    }

    @Test
    public void testMain12() {
        assertTrue(compare("12"));
    }

    @Test
    public void testMain13() {
        assertTrue(compare("13"));
    }

    @Test
    public void testMain14() {
        assertTrue(compare("14"));
    }

    @Test
    public void testMain15() {
        assertTrue(compare("15"));
    }

    @Test
    public void testMain16() {
        assertTrue(compare("16"));
    }

    @Test
    public void testMain17() {
        assertTrue(compare("17"));
    }

    @Test
    public void testMain18() {
        assertTrue(compare("18"));
    }

    @Test
    public void testMain19() {
        assertTrue(compare("19"));
    }

    @Test
    public void testMain20() {
        assertTrue(compare("20"));
    }

    @Test
    public void testMain21() {
        assertTrue(compare("21"));
    }

    @Test
    public void testMain22() {
        assertTrue(compare("22"));
    }

    @Test
    public void testMain23() {
        assertTrue(compare("23"));
    }

    @Test
    public void testMain24() {
        assertTrue(compare("24"));
    }

    @Test
    public void testMain25() {
        assertTrue(compare("25"));
    }

    @Test
    public void testMain26() {
        assertTrue(compare("26"));
    }

    @Test
    public void testMain27() {
        assertTrue(compare("27"));
    }

    @Test
    public void testMain28() {
        assertTrue(compare("28"));
    }

    @Test
    public void testMain29() {
        assertTrue(compare("29"));
    }

    @Test
    public void testMain30() {
        assertTrue(compare("30"));
    }

    @Test
    public void testMain31() {
        assertTrue(compare("31"));
    }

    @Test
    public void testMain32() {
        assertTrue(compare("32"));
    }

    @Test
    public void testMain33() {
        assertTrue(compare("33"));
    }

    @Test
    public void testMain34() {
        assertTrue(compare("34"));
    }

    @Test
    public void testMain35() {
        assertTrue(compare("35"));
    }

    @Test
    public void testMain36() {
        assertTrue(compare("36"));
    }

    @Test
    public void testMain37() {
        assertTrue(compare("37"));
    }

    @Test
    public void testMain38() {
        assertTrue(compare("38"));
    }

    @Test
    public void testMain40() {
        assertTrue(compare("40"));
    }

    @Test
    public void testMain41() {
        assertTrue(compare("41"));
    }

    @Test
    public void testMain42() {
        assertTrue(compare("42"));
    }

    @Test
    public void testMain43() {
        assertTrue(compare("43"));
    }

    @Test
    public void testMain44() {
        assertTrue(compare("44"));
    }

    @Test
    public void testMain45() {
        assertTrue(compare("45"));
    }

    @Test
    public void testMain46() {
        assertTrue(compare("46"));
    }

    @Test
    public void testMain47() {
        assertTrue(compare("47"));
    }

    @Test
    public void testMain48() {
        assertTrue(compare("48"));
    }

    @Test
    public void testMain49() {
        assertTrue(compare("49"));
    }

    @Test
    public void testMain50() {
        assertTrue(compare("50"));
    }

    @Test
    public void testMain51() {
        assertTrue(compare("51"));
    }

    @Test
    public void testMain52() {
        assertTrue(compare("52"));
    }

    @Test
    public void testMain53() {
        assertTrue(compare("53"));
    }

    @Test
    public void testMain54() {
        assertTrue(compare("54"));
    }

    @Test
    public void testMain55() {
        assertTrue(compare("55"));
    }

    @Test
    public void testMain56() {
        assertTrue(compare("56"));
    }

    @Test
    public void testMain57() {
        assertTrue(compare("57"));
    }

    @Test
    public void testMain58() {
        assertTrue(compare("58"));
    }

    @Test
    public void testMain59() {
        assertTrue(compare("59"));
    }

    @Test
    public void testMain60() {
        assertTrue(compare("60"));
    }

    @Test
    public void testMain61() {
        assertTrue(compare("61"));
    }

    @Test
    public void testMain62() {
        assertTrue(compare("62"));
    }

    @Test
    public void testMain63() {
        assertTrue(compare("63"));
    }

    @Test
    public void testMain64() {
        assertTrue(compare("64"));
    }

    @Test
    public void testMain65() {
        assertTrue(compare("65"));
    }

    @Test
    public void testMain66() {
        assertTrue(compare("66"));
    }

    @Test
    public void testMain67() {
        assertTrue(compare("67"));
    }

    @Test
    public void testMain68() {
        assertTrue(compare("68"));
    }

    @Test
    public void testMain69() {
        assertTrue(compare("69"));
    }

}

