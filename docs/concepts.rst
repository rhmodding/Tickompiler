Key Tickflow Concepts / Glossary
================================

Code execution
    Tickflow code consists of a sequence of operations or functions, which are all executed sequentially.
    Some Tickflow operations may redirect the execution to some other location.

Conditional variable
    Tickflow keeps track of a particular variable, which is used in several operations. It can be set by some
    operations, and is generally used by conditional operations such as ``if`` to determine what Tickflow code
    to run.

Threads
    Tickflow is multithreaded. This means that multiple pieces of Tickflow code may be running at the same time.
    A thread is the execution of one such piece of Tickflow code. A synchronous Tickflow function call will
    change the location at which the current thread of execution is running, while an asynchronous call will
    spawn a new thread at the desired location.

Ticks
    A tick is the basic unit of time used in all Tickflow operations. 48 (``0x30``) ticks are generally equal to one beat
    of music.