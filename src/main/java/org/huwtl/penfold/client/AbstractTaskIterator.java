package org.huwtl.penfold.client;

import com.google.common.collect.AbstractIterator;

import java.util.Iterator;
import java.util.Optional;

public abstract class AbstractTaskIterator extends AbstractIterator<Task>
{
    protected final TaskQueryServiceImpl taskQueryService;

    private TasksPage currentPage;

    private Iterator<Task> currentPageIterator;

    public AbstractTaskIterator(final TaskQueryServiceImpl taskQueryService)
    {
        this.taskQueryService = taskQueryService;
    }

    @Override protected Task computeNext()
    {
        if (currentPage == null)
        {
            currentPage = loadPageOfTasks(Optional.empty());
            currentPageIterator = currentPage.tasks.iterator();
        }

        if (currentPageIterator.hasNext())
        {
            return currentPageIterator.next();
        }
        else if (currentPage.nextPage.isPresent())
        {
            moveToNextPage();

            return currentPageIterator.hasNext() ? currentPageIterator.next() : endOfData();
        }
        else
        {
            return endOfData();
        }
    }

    private void moveToNextPage()
    {
        currentPage = loadPageOfTasks(currentPage.nextPage);
        currentPageIterator = currentPage.tasks.iterator();
    }

    protected abstract TasksPage loadPageOfTasks(final Optional<PageReference> pageReference);
}
