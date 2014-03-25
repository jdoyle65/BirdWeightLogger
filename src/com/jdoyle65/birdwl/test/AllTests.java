package com.jdoyle65.birdwl.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ BridgePairTest.class,
	ConfigParserTest.class,
	DataLoggerTest.class })
public class AllTests {

}
