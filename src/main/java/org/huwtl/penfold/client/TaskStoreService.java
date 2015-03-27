package org.huwtl.penfold.client;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TaskStoreService
{
    Task create(NewTask task);

    Task start(Task task) throws ConflictException;

    Task requeue(Task task, Optional<String> reason) throws ConflictException;

    Task reschedule(Task task, LocalDateTime triggerDate, Optional<String> reason) throws ConflictException;

    Task cancel(Task task, Optional<String> reason) throws ConflictException;

    Task close(Task task, Optional<String> reason) throws ConflictException;
}
