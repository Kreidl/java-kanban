package Manager;

import Tasks.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> historyOfTasks = new ArrayList<>();


    @Override
    public ArrayList<Task> getHistory() {
        return historyOfTasks;
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        historyOfTasks.add(task);
        if (historyOfTasks.size() > 10) {
            historyOfTasks.removeFirst();
        }
    }
}
