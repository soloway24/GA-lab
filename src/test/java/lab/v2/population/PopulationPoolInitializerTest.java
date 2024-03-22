package lab.v2.population;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PopulationPoolInitializerTest {

    private static final int POOL_SIZE = 100;

    @Mock
    private PopulationInitializer populationInitializer;
    @Mock
    private PopulationConfiguration populationConfiguration;
    @Mock
    private Population population;
    @InjectMocks
    private PopulationPoolInitializer poolInitializer;

    private PopulationPoolConfiguration poolConfiguration;

    @BeforeEach
    public void init() {
        poolConfiguration = new PopulationPoolConfiguration(populationConfiguration, POOL_SIZE);
        when(populationInitializer.initializePopulation(populationConfiguration)).thenReturn(population);
    }

    @Test
    public void whenInitializePopulationPoolThenSuccess() {
        PopulationPool actualPool = poolInitializer.initializePopulationPool(poolConfiguration);
        assertThat(actualPool.getSize(), equalTo(POOL_SIZE));
    }

}