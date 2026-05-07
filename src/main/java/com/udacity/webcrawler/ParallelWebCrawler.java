package com.udacity.webcrawler;

import com.udacity.webcrawler.json.CrawlResult;
import com.udacity.webcrawler.parser.PageParser;
import com.udacity.webcrawler.parser.PageParserFactory;
import org.checkerframework.common.reflection.qual.Invoke;

import javax.inject.Inject;
import javax.inject.Provider;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.collect.Iterators.limit;
import static java.util.concurrent.ForkJoinTask.invokeAll;

/**
 * A concrete implementation of {@link WebCrawler} that runs multiple threads on a
 * {@link ForkJoinPool} to fetch and process multiple web pages in parallel.
 */
final class ParallelWebCrawler implements WebCrawler {
  private final Clock clock;
  private final Duration timeout;
  private final int popularWordCount;
  private final int maxDepth;
  private final List<Pattern> ignoredUrls;
  private final ForkJoinPool pool;
  private final PageParserFactory parserFactory;

  private final Set<String> visitedUrls =
          ConcurrentHashMap.newKeySet();

  private final Map<String, Integer> counts =
          new ConcurrentHashMap<>();

  @Inject
  ParallelWebCrawler(
      Clock clock,
      @Timeout Duration timeout,
      @PopularWordCount int popularWordCount,
      @TargetParallelism int threadCount,
      @MaxDepth int maxDepth,
      @IgnoredUrls List<Pattern> ignoredUrls,
      PageParserFactory parserFactory) {
    this.clock = clock;
    this.timeout = timeout;
    this.popularWordCount = popularWordCount;
    this.maxDepth = maxDepth;
    this.ignoredUrls = ignoredUrls;
    this.pool = new ForkJoinPool(Math.min(threadCount, getMaxParallelism()));
    this.parserFactory = parserFactory;
  }

  private final class CrawlTask extends RecursiveAction {
      private final String url;
      private final int depth;
      private final Instant deadline;

      private CrawlTask(String url, int depth, Instant deadline) {
          this.url = url;
          this.depth = depth;
          this.deadline = deadline;
      }

      @Override
      protected void compute() {
          if (depth == 0 || Instant.now(clock).isAfter(deadline) ) {
              return;
          }
          for (Pattern pattern : ignoredUrls) {
              if (pattern.matcher(url).matches()) {
                  return;
              }
          }

          if (!visitedUrls.add(url)) {
              return;
          }

          PageParser.Result result = parserFactory.get(url).parse();
          result.getWordCounts().forEach((word, count) ->
                  counts.merge(word, count, Integer::sum));

          List<CrawlTask> subTasks = new ArrayList<>();

          for (String link : result.getLinks()) {
              subTasks.add(
                      new CrawlTask(link, depth - 1, deadline));
          }
          invokeAll(subTasks);
      }
  }

  @Override
  public CrawlResult crawl(List<String> startingUrls) {
    Instant deadline = Instant.now(clock).plus(timeout);
    List<CrawlTask> tasks = new ArrayList<>();
    for(String url : startingUrls){
        tasks.add(
                new CrawlTask(url,maxDepth,deadline));
    }
    ForkJoinTask.invokeAll(tasks);
    Map<String, Integer> popularWords =
            counts.entrySet()
                    .stream()
                    .sorted(
                            Map.Entry.<String, Integer>comparingByValue()
                                    .reversed()
                                    .thenComparing(entry -> entry.getKey().length(), Comparator.reverseOrder())
                                    .thenComparing(Map.Entry::getKey)
                    )
                    .limit(popularWordCount)
                    .collect(Collectors.toMap(
                          Map.Entry::getKey,
                          Map.Entry::getValue,
                          (a, b) -> a,
                          LinkedHashMap::new
      ));

      return new CrawlResult.Builder()
              .setWordCounts(popularWords)
              .setUrlsVisited(visitedUrls.size())
              .build();
  }

  @Override
  public int getMaxParallelism() {
    return Runtime.getRuntime().availableProcessors();
  }
}
