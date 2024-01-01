package com.frank.springbootwithdocker;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TestContainersTest extends BaseTestContainers {

    @Test
    void canStartPostgresDB() {
        assertThat(postgreSQLContainer.isCreated()).isTrue();
        assertThat(postgreSQLContainer.isRunning()).isTrue();
    }
}
