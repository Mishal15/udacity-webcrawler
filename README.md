# udacity-webcrawler

# Udacity Parallel Web Crawler

## Overview

This project is a multithreaded web crawler built in Java as part of the Udacity Advanced Java Programming Techniques course.

The crawler visits web pages, extracts words and links, counts the most popular words, and processes multiple pages in parallel using Java concurrency features.

---

# Features

* Parallel web crawling using `ForkJoinPool`
* Thread-safe URL tracking using `ConcurrentHashMap`
* Functional programming with Java Stream API
* Dynamic proxy based performance profiler
* JSON configuration loading and result writing
* Dependency Injection using Google Guice
* Unit testing with Maven and JUnit

---

# Technologies Used

* Java
* Maven
* ForkJoinPool
* ConcurrentHashMap
* Stream API
* Dynamic Proxy API
* Google Guice
* Jackson JSON
* JUnit 5

---

# Project Structure

```text
src/main/java/com/udacity/webcrawler/
├── json/
├── main/
├── parser/
├── profiler/
├── ParallelWebCrawler.java
├── WordCounts.java
```

---

# How to Run

## 1. Clone Repository

```bash
git clone https://github.com/Mishal15/udacity-webcrawler.git
```

## 2. Navigate to Project

```bash
cd udacity-webcrawler
```

## 3. Build Project

```bash
mvn package
```

## 4. Run Tests

```bash
mvn test
```

## 5. Run the Web Crawler

```bash
java -cp target/udacity-webcrawler-1.0.jar com.udacity.webcrawler.main.WebCrawlerMain src/main/java/com/udacity/webcrawler/main/config/sample_config.json
```

---

# Example Output

```json
{
  "wordCounts": {
    "library": 37,
    "borrow": 33,
    "books": 29
  },
  "urlsVisited": 2
}
```

---

# Key Concepts Implemented

## Parallelism

The crawler processes multiple URLs concurrently using Java `ForkJoinPool` and recursive tasks.

## Thread Safety

Visited URLs are stored in thread-safe collections to avoid duplicate crawling.

## Functional Programming

Word sorting is implemented using:

* Stream API
* Lambda expressions
* Method references

## Dynamic Proxy Profiling

A custom profiler records execution time of methods annotated with `@Profiled`.

---

# Learning Outcomes

This project helped in understanding:

* Java concurrency
* Synchronization
* Dynamic proxies
* Dependency Injection
* Stream processing
* File I/O
* Unit testing
* Maven project structure

---

# Author

Mishal Gupta

GitHub: [https://github.com/Mishal15](https://github.com/Mishal15)
