package cmsc420.testing;

import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import org.junit.Test;


@RunWith(Suite.class)
@Suite.SuiteClasses({
   RegressionTests.class,
   PublicTests.class,
   StudentTests.class,
   DylansTests.class,
})


public class TestAll {


}
