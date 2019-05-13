@echo off
title Crowd Simulation Launcher
setlocal

IF "%PRTI1516E_HOME%"=="" echo WARNING! PRTI1516E_HOME environment variable has not been set

set RTI_JAR=%PRTI1516E_HOME%\lib\prti1516e.jar

set RTI_JAR=%PRTI1516E_HOME%\lib\prti1516e.jar
java -cp "../../../../crowdsim-hla_modules/modules/java/lib/rti_driver.jar;../../../../crowdsim-hla_modules/modules/java/_build/artifacts/src_jar/src.jar;%CLASSPATH%;crowdsim.jar" crowdsimulation.CrowdSimulation sim.config

rem launch the jar with a config file which specifies the properties of this crowd simulation instance
rem java -jar crowdsim.jar sim.config

pause
endlocal