Known Global Tickflow Operations
================================

This is a list of all Tickflow operations which have known functions and have been given a global alias.

.. _macro:

Asynchronous Subroutine (0)
---------------------------

The async_sub function finds a subroutine corresponding to an argument, then
calls it asynchronously (i.e. the code runs simultaneously to the Tickflow code already running).
Async_sub calls have the following form::

    async_sub id, delay, ???

The ``id`` argument is the ID number assigned to the subroutine. It is first taken from a lookup table of
rhythm game-specific IDs, usually starting at ``0x56``, and then from a global list of subroutines, which starts at 0.
``delay`` represents the delay in ticks before the macro is executed.
The third argument is unknown, but is very often ``0x7D0`` (2000) for unknown reasons.
The second and third arguments can be omitted, and default to 0 and ``0x7D0`` respectively.

If the location called by the sub is within the Tickflow file it's called in, ``async_sub`` is replaced with a corresponding
`async_call`_ call.

.. _get_set_async:

Get/Set Async (1)
-----------------

These two operations share the same operation number, 1. They are differentiated by the special argument.
``get_async`` corresponds to ``1<0>``, while ``set_async`` corresponds to ``1<1>``.
``set_async`` stores the location of an asynchronous function into a slot, which can later be accessed and run using
``get_async``. ``set_async`` is of the following form::

    set_async slot, loc

It stores the location ``loc`` into the slot ``slot``. ``get_async`` is of the following form::

    get_async slot

It calls the location stored into slot ``slot`` as an asynchronous function.

.. _async_call:

Async Call Location (2)
-----------------------

``async_call``, operation number 2, takes a location as an argument and runs the Tickflow code at that location
as an asynchronous function. ::

    async_call loc, delay

The asynchronous function at ``loc`` is called after a delay of ``delay`` ticks.

Subroutine (4)
--------------

``sub`` finds a subroutine corresponding to an argument, then calls it synchronously. ::

    sub id

The ``id`` argument is identical to the one in :ref:`macro`.

Call Location (6)
-----------------

``call`` takes a location as an argument and calls the Tickflow code at that location as a synchronous function. ::

    call loc

The synchronous function at ``loc`` is called.

Return (7)
----------

``return`` takes no arguments, but returns from a synchronous function call. That is, when ``return`` occurs in a
synchronous function, execution is returned to the location the function was called from.

Stop (8)
--------

``stop`` stops the current thread of execution.

.. _rest:

Set Conditional Variable (0xA)
------------------------------

``set_condvar`` sets the value of the conditional variable to its first argument. ::

    set_condvar val

Add Conditional Variable (0xB)
------------------------------

``add_condvar`` adds its first argument to the value of the conditional variable. ::

    add_condvar val

Push Conditional Variable (0xC)
-------------------------------

The conditional variable is pushed to a stack containing at most 16 values. For more information about stacks, see
Wikipedia_.

.. _Wikipedia: https://en.wikipedia.org/wiki/Stack_(abstract_data_type)

::

    push_condvar

Pop Conditional Variable (0xD)
------------------------------

The conditional variable is popped from the previously mentioned stack. ::

    pop_condvar

Rest (0xE)
----------

``rest`` pauses execution for a specified number of ticks. ::

    rest duration

Tickflow execution is paused for ``duration`` ticks. 48, or ``0x30``, ticks make one beat.
Note that ``duration`` is actually the special argument for ``rest``, but the syntax is like a regular argument here
for convenience.

Get/Set Rest (0xF)
------------------

``getrest`` and ``setrest`` work similarly to :ref:`get_set_async`: ``setrest`` stores a duration in a slot, to later
be used by ``getrest`` to rest for that duration. ::

    setrest slot, duration

The duration ``duration`` is stored in slot ``slot``. ::

    getrest slot

Execution is paused for the duration previously stored in ``slot``.

Label (0x14)
------------

A label takes only a special argument, and marks this location for use by ``goto``. Can be positioned after a ``goto``. ::

    label id

This location in the file is marked as ``id`` for use by ``goto``.
Note that, like in :ref:`rest`, ``id`` is actually a special argument.

Goto (0x15)
-----------

``goto`` takes only a special argument, and jumps to the corresponding ``label``. It presumably searches for the nearest
label matching the ID. ::

    goto id

Execution jumps to the label with ID ``id``.
Note that, like in :ref:`rest`, ``id`` is actually a special argument.

If, Else, Endif (0x16...0x18)
-----------------------------

Together, these operations form if-blocks, a popular programming construct. ::

    if arg
        // Tickflow code
    else
        // other Tickflow code
    endif

If the value of the conditional variable is equal to ``arg``, then the first block of Tickflow code is executed.
Otherwise, the second block of Tickflow code is executed. The ``else`` block can be omitted entirely, in which case
it is assumed to be empty.

There are also several different variants on ``if``::

    if_neq arg
    if_lt arg
    if_leq arg
    if_gt arg
    if_geq arg

These execute the code if the conditional variable is
not equal, less than, less than or equal, greater than, and greater than or equal to ``arg``, respectively.

Switch, Case, Break, Default, Endswitch (0x19...0x1D)
-----------------------------------------------------

Together, these operations form switch-case statements, another construct commonly found in programming languages. ::

    switch
        case arg1
        // tickflow code
        break
        case arg2
        // more tickflow code
        break
        [...]
        default
        // code
        break
    endswitch

If the value of the condition variable is equal to ``arg1``, then the ``case arg1`` block runs. If the value of the
condition variable is equal to ``arg2``, then the ``case arg2`` block runs, etc. If none of the cases match the value
of the condition variable, the ``default`` block runs. If any ``break`` is omitted, then after running the corresponding
code block, the next case will also be run.

Countdown (0x1E)
----------------

``countdown`` operations implement a countdown using two internal variables; the initial value of the countdown, and the
"progress" of the countdown, which is subtracted from the initial value. ::

    set_countdown num

Sets the initial value to ``num`` and sets the progress to 0. Equivalent to ``0x1E<0>``. ::

    set_countdown_condvar

Sets the initial value to the value of the conditional variable, and sets progress to 0. Equivalent to ``0x1E<1>``. ::

    get_countdown_init

Sets the conditional variable to the initial value of the countdown. Equivalent to ``0x1E<2>``. ::

    get_countdown_prog

Sets the conditional variable to the progress of the countdown. Equivalent to ``0x1E<3>``. ::

    get_countdown

Sets the conditional variable to the countdown value: ``initial - progress``. Equivalent to ``0x1E<4>``. ::

    dec_countdown

Increments the progress variable by 1, therefore decrementing the countdown value by 1. Equivalent to ``0x1E<5>``.

Speed (0x24)
------------

``speed`` sets the speed of the game to a specified fraction of the original speed. This also increases the pitch
of the music. An example of ``speed`` usage can be found in Karate Man Senior, when the game speeds up. ::

    speed val

The speed is set to ``val/256`` of the original speed. For example, ``speed 0x100`` sets the speed to the original speed,
while ``speed 0x120`` sets the speed to 288/256, or 112.5% of the original speed.

Engine (0x28)
-------------

``engine`` sets the game engine to the one corresponding to the argument ID. ::

    engine id

The game engine is set to the engine corresponding to ``id``. Game engines have a set of special tickflow functions which
are specific to that game, as well as a set of macros and/or subroutines.

Set Game to Asset Slot (0x2A)
-----------------------------

This is a set of operations all sharing the same operation number, but being distinguished by different special argument
values. ::

    game_model id, slot
    game_cellanim id, slot
    game_effect id, slot
    game_layout id, slot

These assign a game engine ID to an asset (model, cellanim, effect or layout) slot, to allow the game to load assets
from the correct asset slots when loading a game.
``game_model`` corresponds to ``0x2A<0>``, ``game_cellanim`` to ``0x2A<2>``, ``game_effect`` to ``0x2A<3>`` and
``game_layout`` to ``0x2A<4>``.

.. _model:

Model Asset Management (0x31)
-----------------------------

This is a set of operations differentiated by their special argument, which all share a common theme of being used
to manage the loading of model assets. Model assets are organized into slots starting at slot 1,
where one slot can hold assets for one rhythm game. ::

    set_model slot, str, ???

The first argument is a the slot for the model assets to be loaded into, the second argument is a location in memory
that contains a string, namely the filename of the file containing the assets to be loaded. The third argument is unknown,
but seems to always be 1. ``set_model`` corresponds to ``0x31<0>``. ::

    remove_model slot

Removes the model assets currently loaded into ``slot``. ``remove_model`` corresponds to ``0x31<1>``. ::

    has_model slot

Seems to set the conditional variable to 1 if ``slot`` contains assets, and 0 otherwise. ``has_model`` corresponds
to ``0x31<2>``.

Cellanim Asset Management (0x35)
--------------------------------

Very similarly to :ref:`model`, this set of operations manages cellanim assets. Cellanim assets consist of 2D sprites
and animations thereof. Cellanim assets, similarly to model assets, are organized into slots starting at slot 2, with
each slot holding assets for one rhythm game. ::

    set_cellanim slot, str, ???

The first argument is the slot for the assets to be loaded into, the second argument is a location in memory that contains
the filename of the file to be loaded. The third argument is unknown, but seems to always be ``0xFFFFFFFF``, -1 when
interpreted as a signed integer. ``set_cellanim`` corresponds to ``0x35<0>``. ::

    cellanim_busy slot

Seems to set the conditional variable to 1 if ``slot`` is currently being written to or deleted from, and 0 otherwise.
``cellanim_busy`` corresponds to ``0x35<1>``. ::

    remove_cellanim slot

Removes the cellanim assets currently loaded into ``slot``. ``remove_cellanim`` corresponds to ``0x35<3>``.

Effect Asset Management (0x39)
------------------------------

Similarly to the previous two entries, this set of operations manages effect assets. Effect assets seem to consist of
particle effects, and are organized into slots starting at slot 2, with each slot holding assets for one rhythm game. ::

    set_effect slot, str, ???

This operation has identical functioning to ``set_cellanim``. ``set_effect`` corresponds to ``0x39<0>``. ::

    effect_busy slot

This operation has identical functioning to ``cellanim_busy``. ``effect_busy`` corresponds to ``0x39<1>``. ::

    remove_effect slot

This operation has identical functioning to ``remove_cellanim``. ``remove_effect`` corresponds to ``0x39<7>``.

Layout Asset Management (0x3E)
------------------------------

Similarly to the previous entries, this set of operations manages layout assets. Layout assets are organized into slots
starting at slot 4, though the slots used by stock games and remixes wildly vary. ::

    set_layout slot, str, ???

This operation has identical functioning to ``set_effect`` and ``set_cellanim``. ``set_layout`` corresponds to ``0x3E<0>``. ::

    layout_busy slot

This operation has identical functioning to ``effect_busy`` and ``cellanim_busy``. ``layout_busy`` corresponds to ``0x3E<1>``. ::

    remove_layout slot

This operation has identical functioning to ``remove_effect`` and ``remove_cellanim``. ``remove_layout`` corresponds to ``0x3E<7>``.

Play SFX (0x40)
---------------

This operation plays a sound effect according to an ID. ::

    play_sfx id

A sound effect is played according to ``id``. Where these IDs are defined is not yet clear, though the sound effect
may be played after a tempo-dependent delay, suggesting that these IDs encode additional info, and not only the sound
effect itself.

Set SFX Slot (0x5D)
-------------------

This operation loads sound effects into the specified SFX slot. Sound effects in the loaded assets can thereafter be
played at any time. ::

    set_sfx slot, str

Loads the sound effects corresponding to the group name at the location ``str`` in memory into ``slot``.

Remove SFX (0x5F)
-----------------

This operation removes previously loaded sound effects from the specified SFX slot. ::

    remove_sfx slot

Removes the SFX assets loaded into ``slot``.

Enable/Disable Input (0x6A)
---------------------------

This operation enables or disables all user input. ::

    input flag

Disables input if ``flag`` is 0, enables it if it is 1.

Skill Star (0xAE)
-----------------
::

    star time

A skill star appears, to be collected after ``time`` ticks. Glitchy if no input matches the given time.

Random (0xB8)
-------------

This operation generates a random number and stores it in the conditional variable. ::

    random num

Stores a random number between 0 and ``num`` exclusive in the conditional variable. Note that, like in :ref:`rest`,
``num`` is actually a special variable.