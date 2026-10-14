import com.voznipark.fleet.entity.SystemRefresh;
import com.voznipark.fleet.repository.SystemRefreshRepository;
import com.voznipark.fleet.service.StatusRefreshService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Requires docker-compose up (MySQL on localhost:3307, Kafka on localhost:9094).
 */
@SpringBootTest(classes = com.voznipark.fleet.FleetServiceApplication.class)
class StatusRefreshServiceTest {

    @Autowired
    private StatusRefreshService statusRefreshService;

    @Autowired
    private SystemRefreshRepository systemRefreshRepository;

    @Test
    void triggerManualRefresh_updatesSystemRefreshRow() {
        statusRefreshService.triggerManualRefresh();

        Optional<SystemRefresh> refreshed = systemRefreshRepository.findById("STATUS_REFRESH");
        assertThat(refreshed).isPresent();

        SystemRefresh systemRefresh = refreshed.get();
        assertThat(systemRefresh.getLastRunTs()).isNotNull();
        assertThat(systemRefresh.getLastRunTs().toLocalDate()).isEqualTo(LocalDate.now());
        assertThat(systemRefresh.getLastRunStatus()).isEqualTo("OK");
        assertThat(systemRefresh.getLastRunBy()).isEqualTo("fleet-service");
        assertThat(systemRefresh.getLastRunMessage())
                .contains("vehicles=")
                .contains("drivers=")
                .contains("expiringDocuments=");

        System.out.println("Refresh summary: " + systemRefresh.getLastRunMessage());
    }

    @Test
    void triggerManualRefresh_secondCallSameDaySkips() {
        statusRefreshService.triggerManualRefresh();
        SystemRefresh firstRun = systemRefreshRepository.findById("STATUS_REFRESH").orElseThrow();

        statusRefreshService.triggerManualRefresh();
        SystemRefresh secondRun = systemRefreshRepository.findById("STATUS_REFRESH").orElseThrow();

        assertThat(secondRun.getLastRunTs()).isEqualTo(firstRun.getLastRunTs());
    }
}
