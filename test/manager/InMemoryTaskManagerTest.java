package manager;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest {

    @BeforeEach
    @Override
    public void createNewTaskManager() {
        super.taskManager = new InMemoryTaskManager();
    }
}
