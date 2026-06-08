package com.ke.bella.openapi.job.worker;

import com.ke.bella.openapi.job.queue.TaskWrapper;

public interface TaskExecutor {
    void submit(TaskWrapper task);

    default Integer remainingCapacity() {
        return 1;
    }
}
