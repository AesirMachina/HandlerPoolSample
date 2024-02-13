# Handler Pool Sample Application Readme
February 2024

This repository contains a distributable binary and sample code for the 
[https://github.com/AesirMachina/HandlerPool](Handler Pool Library).

The Handler Pool is a library that provides background threading using for your Android 
applications using a pool of [HandlerThread](https://developer.android.com/reference/android/os/HandlerThread)
and [Handler](https://developer.android.com/reference/android/os/Handler)s.

## The Problem

On Android there are many ways to assign work to a thread:

* [Handler](https://developer.android.com/reference/android/os/Handler), which is used how the main thread of your Android application runs.
* [Thread](https://developer.android.com/reference/kotlin/java/lang/Thread): create a new thread by extending the this class and overriding the ```run``` function.
* [Runnable](https://developer.android.com/reference/kotlin/java/lang/Runnable): implement the Runnable interface and pass it to a Thread.
* [Work Manager](https://developer.android.com/topic/libraries/architecture/workmanager): the recommended library for persisted work. 

Each approach has some advantages and disadvantages: 

| Approach | Advantages | Some Disadvantages/Caveats                                                          |
|----------|------------|-------------------------------------------------------------------------------------|
| Handler | very reliable with known overhead | code needs to be written to respond to detect a destroyed context.          |
| Thread | easy to use | not liecycle aware, and can create orphan threads when the activity or service ends |
| Runnable | see Thread | see Thread                                                                          |
| Work Manager | support for reliable execution after device restarts, constraints, etc. | Requires ramp up time to learn.                                                     |

All of these approaches have a few things in common: 

* none of them provide support for detecting a task that runs longer than expected.  When writing an Android application it's beneficial to create a runtime constraint on every task that runs on a background thread; and, to be notified when a task violates that constraint.
* none of them respond to changes in lifecycle. Work Manager, in particular is designed to run as long as possible even when the lifecyclle changes. But sometimes it's advantageous to respond to lifecycle changes of the underlying component (Activity, Service, Fragment) as it happens.
* it can be challenging to achieve reverse traceability on application requirements. For example, how can you easily determine whether or not a background task meets all the performance requirements of a feature?
* none of these approaches support pausing/resume as the lifecycle of the underlying component changes. 

## The Solution

Handler Pool Library is a solution that attempts to solve all of these problems. Here are some features built into the library:

1. low onboarding time. Handler Pool wraps a pool of handlers. If you know how to use a Handler you know how to use Handler Pool.
2. lifecycle awareness. Handler Pool is attached to a ```LicecycleOwner``` and your work tasks are notified of lifecycle changes as they occur. THis allows you to pause when ```onPause``` is called and resume it when ```onResume``` is called, for example.
3. reverse traceability. Handler Pool encourages you to define work in small, well defined blocks of functionality, which can be spotted easily by anyone on the team and architects as well.
4. performance notificaitons. Handler Pool executes callbacks for tasks that take too long based on three levels: performance warning, performance critical, and performance failure.
5. runtime determinism. Background tasks can be chained together just like they can be with ```Work Manager```.


## Some Related Links

* [Working With WorkManager](https://www.youtube.com/watch?v=83a4rYXsDs0)
