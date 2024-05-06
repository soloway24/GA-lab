package lab.population;

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

    @BeforeEach
    public void init() {
        when(populationInitializer.initializePopulation(populationConfiguration)).thenReturn(population);
    }

    @Test
    public void whenInitializePopulationPoolThenSuccess() {
        PopulationPool actualPool = poolInitializer.initializePopulationPool(populationConfiguration, POOL_SIZE);
        assertThat(actualPool.getSize(), equalTo(POOL_SIZE));
    }

}