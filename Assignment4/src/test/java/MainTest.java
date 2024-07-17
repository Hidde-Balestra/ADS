import models.ClimateTracker;
import models.Measurement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class MainTest {

    ClimateTracker climateTracker;

    @BeforeEach
    private void setup() {
        climateTracker = new ClimateTracker();

        climateTracker.importClimateDataFromVault(ClimateTracker.class.getResource("/test").getPath());
    }

    @Test
    /**
     * This test makes sure the numberOfMeasurementsByStation method returns the correct values
     */
    public void checkTotalMeasurements(){

        //Check if the amount of measurements by station don't contain null values
        assertThat(climateTracker.numberOfMeasurementsByStation().values(), notNullValue());

        //Check if the amount of measurements by station is not empty
        assert !climateTracker.numberOfMeasurementsByStation().values().isEmpty();

        //Check if the found values of the amount of measurements by station match the found values of the amount
        //of measurements by station
        assertThat(climateTracker.numberOfMeasurementsByStation().values(), containsInAnyOrder(0, 0, 5, 488, 609));
    }

    @Test
    /**
     * This test makes sure the totalPrecipitation method returns the correct values
     */
    public void checkTotalPrecipitationBetween(){
        final double actualValueOfTotalPrecipitation = 395.2;

        //Check if the found value returns 0.0 if the startDate is after the endDate
        assert climateTracker.findStationById(380)
                .totalPrecipitationBetween(LocalDate.of(2020, 1, 1),
                        LocalDate.of(2019, 4, 1)) == 0.0;

        //Check if the found value of the total precipitation in Maastricht between 2019-04-01 and 2020-01-01 is not NaN
        assert !Double.isNaN(climateTracker.findStationById(380)
                .totalPrecipitationBetween(LocalDate.of(2019, 4, 1),
                        LocalDate.of(2020, 1, 1)));

        //Check if the found value of the total precipitation in Maastricht between 2019-04-01 and 2020-01-01 is equal
        //to the actual value of the total precipitation in Maastricht between 2019-04-01 and 2020-01-01
        assert climateTracker.findStationById(380)
                .totalPrecipitationBetween(LocalDate.of(2019, 4, 1),
                        LocalDate.of(2020, 1, 1)) == actualValueOfTotalPrecipitation;
    }


    @Test
    /**
     * This test makes sure the totalPrecipitation method returns the correct value
     */
    public void coldestYearTest(){
        final int actualValueColdestYear = 2020;

        //Check if the method found the coldest year (if it can't find it, it returns -1)
        assert climateTracker.coldestYear() != -1;

        //Check if the found value of coldestYear is equal to the actual value of the coldest year.
        assert climateTracker.coldestYear() == actualValueColdestYear;
    }
    
    @Test
    /**
     * This test makes sure the annualAverageTemperature method returns the correct values
     */
    public void annualAverageTemperatureTrendTest(){

        final Map<Integer, Double> emptyMap = new HashMap<>();

        //Check if the method does not return null, since it should contain data
        assert climateTracker.annualAverageTemperatureTrend() != null;

        //Check if the method returns actual values and not just an empty map
        assert !Objects.equals(climateTracker.annualAverageTemperatureTrend(), emptyMap);

        //Check if the found values of the average temperature per year matched the found values of average temperature
        //per year
        assertArrayEquals(new double[] {13.5198, 12.8538, 9.5}, climateTracker.annualAverageTemperatureTrend().values()
                        .stream().mapToDouble(v->v).toArray(), 0.01);

    }

    @Test
    /**
     *This test makes sure annualMaximumTrend returns the correct values
     */
    public void annualMaximumTest(){

        //Check if the outcome of the annualMaximumTrend method using precipitation is not a null value
        assert climateTracker.annualMaximumTrend(Measurement::getPrecipitation) != null;

        //Check if the outcome of the annualMaximumTrend method using precipitation is not an empty map
        assert !climateTracker.annualMaximumTrend(Measurement::getPrecipitation).isEmpty();

        //Check if the outcome of the annualMaximumTrend method using precipitation returns the expected, correct results
        assertArrayEquals(new double[] {40.8, 32.6, 10.7}, climateTracker.annualMaximumTrend(Measurement::getPrecipitation).values()
                .stream().mapToDouble(v->v).toArray(), 0.01);

    }






}
