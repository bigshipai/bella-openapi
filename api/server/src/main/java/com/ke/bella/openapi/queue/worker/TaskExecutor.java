package com.ke.bella.openapi.queue.worker;

import com.ke.bella.openapi.queue.TaskWrapper;

public interface TaskExecutor {
    void submit(TaskWrapper task);

    default Integer remainingCapacity() {
        return 1;
    }
}
