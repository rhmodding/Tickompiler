Game Engines
============

This page contains documentation for game engines. Game engines have sets of game-specific Tickflow operations starting at ``0x100``,
as well as subroutines. Game engines can be loaded in remixes and rhythm games using the ``engine`` operation.

.. contents:: Table of Contents
   :depth: 2

Spaceball (0)
-------------

Spaceball is the first game engine, with the ID 0. It has several ``0x100`` series operations, which are used for cues,
but no subs that would be useful in remixes.

0x100 - Bat object
~~~~~~~~~~~~~~~~~~
::

    0x100 time, type

An object is spawned (shot out of the pipe) depending on ``type``. Legal values of ``type`` are:

- 0, which spawns a baseball;

- 1, which spawns a rice ball;

- 2, which spawns the alien.

``time`` denotes the time in ticks before the batter has to hit the ball; the time before input. If ``time`` is equal
to ``0x30`` (one beat), the low ball animation is used. Otherwise, the high ball animation is used.

0x101 - Batter costume
~~~~~~~~~~~~~~~~~~~~~~
::

    0x101 costume

The batter's costume is changed depending on ``costume``. Legal values of ``costume``, along with their appearances
in the standard Spaceball cellanim, are:

- 0, which is the default costume;

- 1, which is the "red head" costume;

- 2, which is the bunny costume.

0x102 - Camera zoom
~~~~~~~~~~~~~~~~~~~
::

    0x102<type> distance, time

The camera zooms in or out. ``distance`` is the distance the camera will be at the end of the zoom. It ranges between
0 and ``0xA`` (10), with 4 being the default camera position. The zoom will occur over ``time`` ticks.
``type`` controls the way the zoom is interpolated: 1 gives a linear interpolation, or a rough zoom, and 2 gives a
cubic interpolation, a smooth zoom.

0x103 - Alien animation
~~~~~~~~~~~~~~~~~~~~~~~
::

    0x103<flag>

``0x103`` controls the animation of the alien in the middle of the screen, namely, ``0x103<0>`` makes it pop out
of its ship, while ``0x103<1>`` makes it return to its ship.

Clappy Trio (1)
---------------

Clappy Trio uses ``0x100`` series operations for the basic building blocks, but bundles them together into subs for
convenience.

0x100 - Clap cue
~~~~~~~~~~~~~~~~
::

   0x100 time

After ``2*time`` ticks, the player has to press the A button to clap.

0x101 - Beat animation
~~~~~~~~~~~~~~~~~~~~~~
::

   0x101 ???

``0x101`` does the beat animation for the clappy trio (head bob). The argument's purpose is unknown, though it seems to be
1 at the end of a set of beat animations, and 0 elsewhere. ::

   0x101<1> num

``0x101<1>`` precedes a set of ``num`` beat animations. Purpose unknown.

0x102 - Ready stance
~~~~~~~~~~~~~~~~~~~~
::

   0x102<type>

``0x102`` does the ready stance animation for the clappy trio. If ``type`` is 0, the animation is the regular ready stance.
If ``type`` is 1, the animation is the "determined" ready stance.

0x103 - Clap animation
~~~~~~~~~~~~~~~~~~~~~~
::

   0x103 num

``0x103`` does the clap animation and sound effects for a single member of the trio. ``num`` is the number of the member
to do the clap animation for, from left to right starting at 0. Note that the player's member is unaffected by this operation.

List of subs
~~~~~~~~~~~~

0x56 (async)
   Does a full clap cue, with the claps spaced two beats apart. (no ready stance)

0x57 (async)
   Does a full clap cue, with the claps spaced one beat apart. (no ready stance)

0x58 (async)
   Does a full clap cue, with the claps spaced a half beat apart. (no ready stance)

0x59 (async)
   Does a full clap cue, with the claps spaced a quarter beat apart. (no ready stance)

0x5A (async)
   Does a full clap cue, with the claps spaced an eighth beat apart. (no ready stance)

0x5B (async)
   Does a full clap cue, with the claps spaced two thirds of a beat apart. (no ready stance)

Sneaky Spirits (2)
------------------

Sneaky Spirits bundles ``0x100`` series operations into subs.

0x100 - Shoot cue (?)
~~~~~~~~~~~~~~~~~~~~~
::

   0x100 ???

A beat after ``0x100``, the input to shoot a spirit occurs. The purpose of the argument is unknown.

0x101 - Draw bow
~~~~~~~~~~~~~~~~
::

   0x101

``0x101`` draws the bow in preparation for a shot.

0x102 - Pop-up spirit
~~~~~~~~~~~~~~~~~~~~~
::

   0x102 pos

``0x102`` makes a spirit pop up at the specified position on the fence. 0 is the leftmost position and 6 is the rightmost.

0x103 - Close door
~~~~~~~~~~~~~~~~~~
::

   0x103

``0x103`` closes the door.

0x104 - Spirit height
~~~~~~~~~~~~~~~~~~~~~
::

   0x104 height

``0x104`` sets the height of the spirit, on a scale from 0 to ``0x100`` (256).

0x105 - Spirit position (?)
~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x105 pos

``0x105`` sets whether the spirit is behind the fence or in the hitzone. If ``pos`` is 1 it's behind the fence,
if it's 0 it's in the hitzone.

0x107 - Reset game speed
~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x107

``0x107`` resets the game speed after a spirit was hit.

List of subs
~~~~~~~~~~~~

These are all synchronous subroutines.

0x56
   8-beat spirit cue that stays at max height.

0x57
   8-beat spirit cue that drops off to 0 height slowly.

0x58
   8-beat spirit cue that drops off to 0 height quickly.

0x59
   8-beat spirit cue that drops off to 0 height very quickly.

0x5A
   8-beat spirit cue that starts at 0 height and rises slowly.

0x5B
   8-beat spirit cue that starts at 0 height and rises before dropping back down.

0x5C
   8-beat spirit cue that starts at a low height and drops off slightly before rising.

0x5D
   8-beat spirit cue that alternates between low and high height.

0x5E
   8-beat spirit cue that stays at 0 height.

0x5F
   7-beat spirit cue that stays at max height.

0x60
   7-beat spirit cue that drops off to 0 height slowly.

0x61
   7-beat spirit cue that drops off to 0 height quickly.

0x62
   7-beat spirit cue that starts at 0 height and rises slowly.

Rhythm Tweezers (3)
-------------------

Rhythm Tweezers does not pack cues into subs, likely because of the large variety of patterns. It uses
``0x100`` series operations.

0x100 - Spawn Hair
~~~~~~~~~~~~~~~~~~
::

   0x100 type

``0x100`` spawns a hair, to be plucked 4 beats later. If ``type`` is 0, spawns a normal hair. If ``type`` is 3, spawns
a long hair.

0x101 - Reset Hair Position
~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x101

Resets the position for spawning of hairs. Usually used at the beginning of a pattern.

0x102 - Spawn Tweezers
~~~~~~~~~~~~~~~~~~~~~~
::

   0x102

Spawns tweezers. Used 2 beats after the start of a pattern.

0x105 - Set Vegetable Type
~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x105 type

Sets the type of the next vegetable to appear. 0 gives an onion, 1 a potato.

0x107 - Don't Peek (unused)
~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x107 type

Makes a sign appear that covers part or all of the hairs. 0 spawns a sign that covers both sides, 1 spawns a sign that covers
the right side, and 2 spawns a sign that covers the left side. While normally the signs automatically despawn after
a pattern, ``0x107<1>`` also despawns them.

Note that this entire command is unused in itself, as well as the graphics associated with it.

Extra notes
~~~~~~~~~~~

At the end of a pattern (4 beats after the start), the following code is found::

   rest 0xA2	// 3.375 beats
   0x108<1>
   if 0
       0xA1<2>
       if 1
           0x106 0
           if 1
               0x105 X
           else
               0x103
           endif
       else
           0x105 X
       endif
   endif
   rest 0x18	// 0.5 beats
   rest 6	// 0.125 beats

The logic and functioning of most of this is unknown, however, the ``0x105`` s here change the type of the next vegetable.
Note as well that input is usually disabled using ``input`` at the start of a pattern, and re-enabled 3 beats later.

Bouncy Road (4)
---------------

Bouncy Road groups ``0x100`` series operations into subs. It is necessary to do so, since only one ball can be managed
per Tickflow thread.

0x100 - Spawn/Bounce Ball
~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x100 time

``0x100`` spawns a ball with a bounce duration of ``time`` ticks.

::

   0x100<1>

``0x100<1>`` bounces the ball.

List of subs
~~~~~~~~~~~~

These are all asynchronous subroutines.

0x56
   Spawns a ball with half-beat bounces.

0x57
   Spawns a ball with 2/3-beat bounces.

0x58
   Spawns a ball with 1-beat bounces.

0x59
   Spawns a ball with 2-beat bounces.

Marching Orders (5)
-------------------

Marching Orders groups ``0x100`` series operations into subs.

0x100 - Cue Input
~~~~~~~~~~~~~~~~~
::

   0x100 time, type

Sets up an input cue to be pressed after ``time`` ticks. Legal values of ``type`` are:

- 0: March input

- 1: Halt input

- 2: Right turn input

- 3: Left turn input

0x101 - Raise Legs
~~~~~~~~~~~~~~~~~~
::

   0x101

All marchers raise their legs in preparation for marching.

0x102 - Beat Animation
~~~~~~~~~~~~~~~~~~~~~~
::

   0x102

All marchers do the beat animation (tip toe).

0x103 - Reset stance
~~~~~~~~~~~~~~~~~~~~
::

   0x103

Resets all marchers' stances after a ``0x102``.

0x104 - March animation
~~~~~~~~~~~~~~~~~~~~~~~
::

   0x104<pos>

A single marcher, determined by ``pos``, marches once. ``pos`` ranges from 1 to 3, though usually all are used at once.

0x105 - Halt animation
~~~~~~~~~~~~~~~~~~~~~~
::

   0x105<pos>

A single marcher, determined by ``pos``, halts. ``pos`` ranges from 1 to 3, though usually all are used at once.

0x106 - Right turn animation
~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x106<pos>

A single marcher, determined by ``pos``, turns right. ``pos`` ranges from 1 to 3, though usually all are used at once.

0x107 - Left turn animation
~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x107<pos>

A single marcher, determined by ``pos``, turns left. ``pos`` ranges from 1 to 3, though usually all are used at once.

0x108 - Commander Mouth Movement
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x108

The commander moves his mouth.

0x109 - Activate Conveyor Belt
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x109

The conveyor belt activates.

List of subs
~~~~~~~~~~~~

These are all asynchronous subroutines.

0x56
   The sound effect for the commander saying "TURN!", followed by the right turn cue. Does not include "Right face..."

0x57
   The sound effect for the commander saying "TURN!", followed by the left turn cue. Does not include "Left face..."

0x58
   The sound effect for the commander saying "MARCH!", followed by one march cue. Does not include "Attention..."

0x59
   The sound effect for the commander saying "HALT!", followed by the halt cue. Does not include "Attention..."

Night Walk (6)
--------------

Night Walk only has one useful sub, and uses ``0x100`` series operations for everything else.

0x100 - Spawn Platform
~~~~~~~~~~~~~~~~~~~~~~
::

   0x100 time, type

Spawns a platform that will take ``time`` ticks to reach the player (requiring an A press at that time). ``type`` determines
what pops up when the player successfully jumps on the platform. Legal values for ``type`` are:

- 0: Heart flower

- 1: Lollipop

- 2: Umbrella

- 3: Heart flower, with a slightly different sound to 0.

- 4: Star wand (final platform)

- 5: Edge platform (after final platform)

0x101 - Staircasing state
~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x101 state

``0x101`` sets the state of staircasing for newly spawned platforms. Legal values for ``state`` are:

- 0: No staircasing

- 1: Full staircasing

- 2: Random staircasing

0x103 - Balloons
~~~~~~~~~~~~~~~~
::

   0x103

``0x103`` spawns 7 balloons for Play-Yan to hold onto. ::

   0x103<1> num

``0x103<1>`` pops one of the balloons according to ``num``. ``num`` ranges from 0 to 6.

0x105 - Death check
~~~~~~~~~~~~~~~~~~~
::

   0x105

Sets the conditional variable to 1 if Play-Yan has fallen into a pit, 0 otherwise.

List of subs
~~~~~~~~~~~~

0x56 (async)
   Pops Play-Yan's balloons one after another, making a count-in.

Quiz Show (7)
-------------

Quiz Show groups several things into subroutines.

0x100 - Contestant Actions
~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x100

The contestant puts his hands on the buttons. ::

   0x100<1>

The contestant readies his hands for pressing. ::

   0x100<2>

The contestant's face returns to normal. ::

   0x100<3>

The contestant's face becomes happy. ::

   0x100<4>

The contestant's face becomes sad.

0x101 - Quizmaster Actions
~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x101

The quizmaster puts his hands on the buttons. ::

   0x101<1>

The quizmaster readies his hands for pressing. ::

   0x101<2>

The quizmaster presses the A button, incrementing his counter. ::

   0x101<3>

The quizmaster presses the + button, incrementing his counter. ::

   0x101<4>

The quizmaster's face returns to normal. ::

   0x101<5>

The quizmaster's face becomes happy. ::

   0x101<6>

The quizmaster's face becomes sad.

0x102 - Counter Actions
~~~~~~~~~~~~~~~~~~~~~~~
::

   0x102

Resets the counters to 0. ::

   0x102<1>

Hides the quizmaster's counter (number becomes ??). ::

   0x102<2>

Reveals the quizmaster's counter. ::

   0x102<3>

Sets the conditional variable to 1 if the quizmaster's and contestant's counters match, and 0 if they don't.

0x103 - Spotlight
~~~~~~~~~~~~~~~~~
::

   0x103 flag

If ``flag`` is 1, the spotlight focusing on the quizmaster's podium turns on. If 0, it turns off.

0x104 - Timer Actions
~~~~~~~~~~~~~~~~~~~~~
::

   0x104 flag

If ``flag`` is 1, shows the timer. If 0, hides the timer. ::

   0x104<1> time

Sets the timer duration to ``time`` ticks and starts the timer.

0x105 - Scoring
~~~~~~~~~~~~~~~
::

   0x105

Start recording player presses for scoring purposes. ::

   0x105<1>

Start recording quizmaster presses for scoring purposes. ::

   0x105<2>

Stop recording quizmaster or player presses for scoring purposes. ::

   0x105<3> score

Add points to the rhythm game score on a scale up to ``score`` depending on player performance.

0x106 - Skill Star Score Criterium
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x106

Unknown. ::

   0x106<1>

Sets the conditional variable to 1 if the player has reached a score of 90 (?), 0 otherwise. This is used to determine
whether to award the skill star at the end of the rhythm game.

List of subs
~~~~~~~~~~~~

These are all synchronous subroutines.

0x56
   Hides the quizmaster's counters, resets the counters and readies the quizmaster's hands.

0x57
   The quizmaster puts his hands on the buttons, a text box displays saying "Go ahead.", the timer appears, the player's
   hands are readied, input is enabled, scoring starts recording player presses, and a sound effect plays
   signaling the player to start.

0x58
   Input is disabled, input recording stops, and the answer is revealed.

0x59
   Quizmaster pattern for practice question #1.

0x5A
   Quizmaster pattern for practice question #2.

0x5B
   Quizmaster pattern for practice question #3.

0x5C
   Quizmaster pattern for question #4.

0x5D
   Quizmaster pattern for question #5.

0x5E
   Quizmaster pattern for question #6.

0x5F through 0x64
   Several unused patterns.

0x65
   Random button presses for use at the end of patterns.

Bunny Hop (8)
-------------

Bunny Hop does not use any subs for cues.

0x100 - Spawn Animal
~~~~~~~~~~~~~~~~~~~~
::

   0x100 type, delay

Spawns an animal for the bunny to hop on. The animal will spawn such that the bunny will reach it after ``delay`` ticks.
Legal values for ``type`` are:

- 0: Turtle

- 1: Dark turtle

- 2: Small whale (2-beat rest)

- 3: Vertical whale

- 4: Big whale (8-beat rest)

- 5: Whale tail (4-beat rest)

- 6: Final whale

0x101 - Hop on ground
~~~~~~~~~~~~~~~~~~~~~
::

   0x101<1> ???

The bunny hops on the ground. The argument is unknown, but always 0.

Rat Race (9)
------------

Rat Race makes use of subs for some cues.

0x100 - Cue
~~~~~~~~~~~
::

   0x100 type, time

An input cue will happen depending on ``type`` after ``time`` ticks. Legal values for ``type`` are:

- 0: Start holding

- 1: Release button

0x101 - Background Movement
~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x101 flag

If ``flag`` is 0, stops background movement, else starts background movement.

0x102 - Rat Animations
~~~~~~~~~~~~~~~~~~~~~~
::

   0x102 type

Does an animation for all rats. Legal values of ``type`` are:

- 0: Hiding

- 1: Running

- 2: Crouching (in preparation for running)

0x104 - Cat Animations
~~~~~~~~~~~~~~~~~~~~~~
::

   0x104 type

Does an animation for the cat. Legal values of ``type`` are:

- 0: Left paw (our perspective) put on the table.

- 1: Right paw put on the table.

- 2: Head pops up

- 3: Head pops down

- 4: Look straight ahead

- 5: Look left (our perspective)

- 6: Look right

- 7: Close eyes

- 8: Paws back down (?)

0x105 - Speed Boost
~~~~~~~~~~~~~~~~~~~
::

   0x105 type

Sets what happens after you release the button (speed boost). Values for ``type`` are:

- 1: Normal speed boost

- 2: Large speed boost

Every other value gives no speed boost.

0x106 - Spawn Foreground Object
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x106 delay, type

Spawns a foreground object, usually cover for the rats. It is spawned such that the player reaches it in ``delay`` ticks.
Values for ``type`` are:

- 0: Regular foreground object

- 1: End of game (cheese etc.)

0x107 - Stoplight Control
~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x107

The front rat pulls up the stoplight. ::

   0x107<1>

The front rat puts away the stoplight. ::

   0x107<2> type

Changes the stoplight display according to ``type``. Values for ``type`` are:

- 0: Left light on (orange)

- 1: Middle light on (orange) ::

   0x107<3>

Turns all lights on the stoplight red. ::

   0x107<4>

Turns all lights on the stoplight green. ::

   0x107<5>

Turns all lights on the stoplight off. ::

   0x107<6>

The front rat drops the stoplight.

List of subs
~~~~~~~~~~~~

These are all asynchronous subroutines.

0x56
   Sets up a hold cue for 4 beats after the start of the sub, including cat animations.

0x57
   Sets up a release cue for 4 beats after the start of the sub, including cat animations.

0x58
   Stoplight count for hold cue; timed such that the input should be 3 beats after the start of the sub.

0x59
   Stoplight count for release cue; timed such that the input should be 3 beats after the start of the sub.

Power Calligraphy (0xA)
-----------------------

0x100 - Input
~~~~~~~~~~~~~
::

   0x100

One beat after ``0x100``, an A press input is required.

0x101 - Pattern Actions
~~~~~~~~~~~~~~~~~~~~~~~
::

   0x101 pattern

Sets the pattern on the page. Values for ``pattern`` are:

- 0: 己

- 1: 力

- 2: 寸

- 3: 心

- 4: レ

- 5: 、

- 6: Face pattern (final pattern)

- 7: Final page (完) ::

   0x101<1> num

Sets the next section of the pattern to be written onto the page. This is part of one of the lines, and will be filled
in with the next ``0x105``.

0x102 - Page Movement
~~~~~~~~~~~~~~~~~~~~~
::

   0x102 x, y

Moves the page ``x`` units right and ``y`` units down.

0x103 - Turn page
~~~~~~~~~~~~~~~~~
::

   0x103 type

Turns the page. If ``type`` is 1, the page turns slowly, if 0 it turns normally.

0x104 - Cue action
~~~~~~~~~~~~~~~~~~
::

   0x104 type

Sets which kind of movement will happen for the next A input. Values for ``type`` are:

- 0: The stroke in the 己 pattern

- 2: The stroke in the 力 pattern

- 3: The stroke in the 寸 pattern

- 4: The dot in the 寸 pattern

- 5: The stroke in the 心 pattern

- 7: The dot in the 心 pattern

- 8: The stroke in the レ pattern

- 9: The dot in the 、 pattern

- 0xA: The stroke in the face (final) pattern

0x105 - Brush Actions
~~~~~~~~~~~~~~~~~~~~~
::

   0x105 x, y, flag

Moves the brush to ``x`` units right and ``y`` units down from the middle of the paper. If ``flag`` is 1, moves
with brush on the paper, else moves with brush above paper. ::

   0x105<1> type

Does an animation in preparation for a cue. Values for ``type`` are:

- 0: Presses brush into paper in preparation for a stroke cue.

- 1: Lifts brush in preparation for a dot cue. ::

   0x105<2> flag

Turns hand red if ``flag`` is 1, turns hand back to normal if 0. ::

   0x105<3>

Unknown. ::

   0x105<4>

Unknown, tends to appear alongside brush movements and ``0x105<1>``. ::

   0x105<5>

Unknown, appears alongside ``0x105<1> 1``.

0x106 - Dancers
~~~~~~~~~~~~~~~
::

   0x106<1> type

Sets the animation for the dancers on the sides of the page. Values for ``type`` are:

- 1: Spawns dancers; default animation

- 3: Bowing animation

- 4: Sitting animation

List of subs
~~~~~~~~~~~~

These are all synchronous subroutines.

0x56
   Ready 力 page

0x57
   力 pattern

0x58
   Ready 己 page

0x59
   己 pattern

0x5A
   Ready 寸 page

0x5B
   寸 pattern

0x5C
   Ready 心 page

0x5D
   心 pattern

0x5E
   Ready レ page

0x5F
   レ pattern

0x60
   Ready 、 page

0x61
   、 pattern

0x62
   Ready face (final) page

0x63
   Face (final) pattern

0x64
   Ready 力 page (unused)

0x65
   Swingy 力 pattern (unused)

0x66
   Ready 己 page (unused)

0x67
   Swingy 己 pattern (unused)

0x68
   Ready 寸 page (unused)

0x69
   Swingy 寸 pattern (unused)

0x6A
   Ready 心 page (unused)

0x6B
   Swingy 心 pattern (unused)

0x6C
   Ready レ page (unused)

0x6D
   Swingy レ pattern (unused)

0x6E
   Ready 、 page (unused)

0x6F
   Swingy 、 pattern (unused)

0x70
   Ready face (final) page (unused)

0x71
   Swingy face (final) pattern (unused)

Space Dance (0xB)
-----------------

0x100 - Cue
~~~~~~~~~~~
::

   0x100 time, type

Sets up a button press after ``time`` ticks. Values for ``type`` are:

- 0: D-pad right (pose)

- 1: D-pad down (sit)

- 2: A (punch)

0x101-0x107 - Space Dancers Animations
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
All the following animations include associated sound effects. ::

   0x101<pos>

Beat (bob) animation for the ``pos`` th dancer from the right. ::

   0x102<pos>

Pose animation. ::

   0x103<pos>

Sit animation. ::

   0x104<pos>

Punch animation. ::

   0x105<pos>

Pose preparation animation. ::

   0x106<pos>

Sit preparation animation. ::

   0x107<pos> ???

Punch preparation animation (clap hands). The argument is unknown, but is 1 for the second clap and 0 for others.

0x108-0x110 - Space Gramps Animations
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x108

Space Gramps beat (bob) animation. ::

   0x109

Space Gramps pose animation. ::

   0x10A

Space Gramps sit animation. ::

   0x10B

Space Gramps punch animation. ::

   0x10C

Space Gramps pose preparation animation. ::

   0x10D

Space Gramps sit preparation animation. ::

   0x10E<hand>

Space Gramps punch preparation animation (fist pump). If ``hand`` is 1, uses left hand, if 0, uses right hand. ::

   0x10F

Space Gramps starts talking. ::

   0x110

Space Gramps stops talking.

0x111 - Background Movement
~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x111 hspeed, hdir, vspeed, vdir

Sets background movement. Sets horizontal speed to ``hspeed`` (unit unknown), horizontal direction to right if ``hdir``
is 0, left if ``hdir`` is 1. Sets vertical speed to ``vspeed``, vertical direction to down if ``vdir`` is 0, up if
``vdir`` is 1.

List of subs
~~~~~~~~~~~~
All the following are asynchronous subroutines. Note that these assume that ``getrest 0`` and ``getrest 1``
are set to appropriate values that add up to one beat. In Space Dance, both are a half-beat, ``0x18`` ticks. In Cosmic
Dance, ``getrest 0`` is ``0x20`` ticks and ``getrest 1`` is ``0x10`` ticks.

0x56
   A full punch, such that the input occurs one beat and ``getrest 0`` ticks later, with regular voice SFX.

0x57
   A full punch, with Space Gramps voice SFX.

0x58
   A full punch, with both regular and Space Gramps voice SFX.

0x59
   A full pose, such that the input occurs one beat later, with regular voice SFX.

0x5A
   A full pose, with Space Gramps voice SFX.

0x5B
   A full pose, with both regular and Space Gramps voice SFX.

0x5C
   A full sit, such that the input occurs one beat later, with regular voice SFX.

0x5D
   A full sit, with Space Gramps voice SFX.

0x5E
   A full sit, with both regular and Space Gramps voice SFX.

0x5F
   Space Gramps punch animation, including preparation. This is a sub because the timing depends on the values of
   ``getrest 0`` and ``getrest 1``.

Tap Trial (0xC)
---------------

0x100 - Cue
~~~~~~~~~~~
::

   0x100

Sets up an A button press one beat later.

0x101 - Beat animation
~~~~~~~~~~~~~~~~~~~~~~
::

   0x101

Does a beat (bob) animation for all characters.

0x102 - Animations
~~~~~~~~~~~~~~~~~~
::

   0x102 type

Does an animation. Animations include associated sound effects. Values for ``type`` are:

- 0: Ready single tap

- 1: Ready double tap (pose only)

- 4: Single tap

- 5: Tap to the left (part of double tap)

- 6: Tap to the left (part of triple tap)

- 7: Tap to the right (part of triple tap)

- 8: Ready triple tap (part 1)

- 9: Crouch down (jump preparation)

- 0xA: Jump up (type 1; used when landing into crouch)

- 0xB: Jump up (type 2)

- 0xC: Land from jump

- 0xE: Land into crouch

- 0xF: double tap "ook"

- 0x10: Ready triple tap (part 2)

0x104 - Unknown
~~~~~~~~~~~~~~~
::

   0x104

0x105 - Giraffe
~~~~~~~~~~~~~~~
::

   0x105 1

The giraffe appears.

0x106 - Giraffe thought
~~~~~~~~~~~~~~~~~~~~~~~
::

   0x106

Removes giraffe thought bubble. ::

   0x106<1> ???

Conditionally makes the giraffe think a random message if the player got the last input. The argument can be 0 through 2.

0x107 - Custom giraffe thought
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x107<1> string

The giraffe thinks a message from the script, namely the message corresponding to the name pointed to by ``string``.

::

   0x107<2>

Removes giraffe thought bubble.

0x108 - Background acceleration
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x108

Turns on background acceleration. ::

   0x108<1>

Turns off background acceleration.

0x109 - Unknown
~~~~~~~~~~~~~~~
::

   0x109

Unknown purpose, appears at the end of Tap Trial, but not Tap Trial 2.

List of subs
~~~~~~~~~~~~
All the following are synchronous subroutines.

0x56
   Full single tap

0x57
   Full double tap

0x58
   Jump preparation

0x59
   Jump into crouch

0x5A
   Jump into pose

0x5B
   Full triple tap

Ninja Bodyguard (0xD)
---------------------

0x100 - Cue arrow
~~~~~~~~~~~~~~~~~
::

    0x100 time

Cues an arrow to be sliced after ``time`` ticks.

0x101 - Change scene
~~~~~~~~~~~~~~~~~~~~
::

    0x101<scene>

Changes the scene. Values for ``scene`` are:

- 0: View of archers

- 1: Middle view (intro cinematic)

- 2: View of lord/ninja

0x102 - Archer control
~~~~~~~~~~~~~~~~~~~~~~
::

    0x102 num

Places ``num`` archers. Up to 6 archers are supported; 7 or more results in many off-screen archers. ::

    0x102<1> pos

The ``pos`` th archer from the right, starting at 0, draws their bow. If ``pos`` is -1 (which it almost always is), all archers
draw their bows. ::

    0x102<2> pos

The ``pos`` th archer from the right, starting at 0, releases their bow and shoots an arrow.

0x104 - Intro animations
~~~~~~~~~~~~~~~~~~~~~~
::

    0x104

The enemy arrow flies from one tower to another. ::

    0x104<1>

The enemy arrow lands on the player's wall. ::

    0x104<2>

The ninja falls into position. (Used in remixes for the ninja to appear during transition) ::

    0x104<3>

The ninja gets his sword ready. ::

    0x104<4> time

The sensei walks to the center of the wall in ``time`` ticks. ::

    0x104<5>

The sensei faces the other way in confusion.

Airboarder (0xE)
----------------

0x100 - Spawn blocks
~~~~~~~~~~~~~~~~~~~~
::

    0x100 time, type

Spawns blocks (along with the corresponding button input), such that the player reaches them after ``time`` ticks.
Values for ``type`` are:

- 0: Hop

- 1: Squat

0x101 - Switch camera
~~~~~~~~~~~~~~~~~~~~~
::

    0x101 cam

Switches to camera number ``cam``. There are three cameras, 0 through 2.

0x102 - Instant camera control
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

    0x102 cam, pos, var

Sets the position of camera number ``cam`` to a preset position depending on ``pos`` and ``var``. ::

    0x102<1> cam, pos, var

Sets the focus (where camera is looking) of camera number ``cam`` to a preset position depending on ``pos`` and ``var``. ::

    0x102<2> cam, x, y, z

Sets the position of camera number ``cam`` to a vector defined by ``x``, ``y`` and ``z``, relative to the frontmost
airboarder(?) ::

    0x102<3> cam, x, y, z

Sets the focus of camera number ``cam`` to a vector defined by ``x``, ``y`` and ``z``. ::

    0x102<4> cam, ???

Unknown, maybe zoom?

0x103 - Smooth camera control
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

0x103 is identical to 0x102, with the difference that 0x103 camera changes occur over time, and two extra arguments
are added, ``interp`` and ``time``. ``interp`` determines the interpolation used to move the camera smoothly.
``time`` determines how long the movement takes. For example, ``0x102 cam, pos, var`` turns into
``0x103 cam, interp, time, pos, var``.

0x105 - Airboarder Animation
~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

    0x105 pos

Does the beat animation for the ``pos`` th airboarder, starting from 0. ::

    0x105<1> pos

Does the ducking animation for the ``pos`` th airboarder from the front, starting from 0. ::

    0x105<2> pos

The ``pos`` th airboarder from the front, starting from 0, stops ducking. ::

    0x105<3> pos

The ``pos`` th airboarder from the front, starting from 0, starts the charging animation. ::

    0x105<4> pos

The ``pos`` th airboarder from the front, starting from 0, jumps.

0x106 - Charging
~~~~~~~~~~~~~~~~
::

    0x106 flag

Sets whether the airboarders are charging up for a jump. If ``flag`` is 1, it will be possible to charge and jump, otherwise
not.

List of subs
~~~~~~~~~~~~

All the following are asynchronous subroutines.

0x56
    Beat animations every beat, forever.

0x57
    A full duck cue, such that the input is 28 beats later.

0x58
    A full duck cue enabling charging, such that the input is 28 beats later.

0x59
    A full jump cue without voice SFX at the start, such that the input is 28 beats later.

0x5A
    A full jump cue with voice SFX at the start, such that the input is 28 beats later.

Lockstep (0xF)
--------------

0x100 - Cue
~~~~~~~~~~~
::

    0x100 type, time

Sets up a button input for after ``time`` ticks. Values for ``type`` are:

- 0: Off-beat (to the right)

- 1: On-beat (to the left)

0x101 - Beat animation
~~~~~~~~~~~~~~~~~~~~~~
::

   0x101

Does the beat animation for all Stepswitchers.

0x102 - Step animation
~~~~~~~~~~~~~~~~~~~~~~
::

   0x102

Does the step/cue animation for all Stepswitchers except the player's.

0x103 - Set Direction
~~~~~~~~~~~~~~~~~~~~~
::

   0x103 dir

Sets the direction in which all Stepswitchers will step. If ``dir`` is 0, it's offbeat/to the right. If ``dir`` is 1, it's
onbeat/to the left.

0x104 - Change View
~~~~~~~~~~~~~~~~~~~
::

   0x104 type

Sets the view (zoom in most cases). Values for ``type`` are:

- 0: Regular zoom level.

- 1: Zoomed out.

- 2: Zoomed out further.

- 3: Zoomed out yet further.

- 4: Zoomed out the furthest (portraits are visible).

- 5: Practice view.

0x105 - Background color
~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x105 color

Sets the color of the background. Values for ``color`` are:

- 0, 2, 4, 6: Pink

- 1, 3, 5, 7: Purple

List of subs
~~~~~~~~~~~~
All the following are asynchronous subroutines.

0x56
   On-beat marching for 4 beats, starting the next beat.

0x57
   Transition from on-beat to off-beat marching, takes 4 beats, starting the next beat.

0x58
   Off-beat marching for 4 beats, starting the next beat.

0x59
   Transition from off-beat to on-beat marching, takes 4 beats, starting the next beat.

0x5A
   ``0x104 0``.

0x5B
   ``0x104 1``.

0x5C
   ``0x104 2``.

0x5D
   ``0x104 3``.

0x5E
   ``0x104 4``.

0x5F
   ``0x104 5``.

0x60
   Background transition to off-beat, to be called one beat after 0x57.

0x61
   Background transition to on-beat, to be called one beat after 0x59.

0x62
   Voice clip for start of on-beat marching ("Hai!").

0x63
   Voice clip for transition to off-beat ("Hai hai hai ha-HA!").

0x64
   Voice clip for off-beat marching after transition ("Hop hop hop hop").

0x65
   Voice clip for transition to on-beat ("Hm-ha hm-ha").

0x66
   Voice clip for transition to on-beat with hops ("Hop hop hm-ha hm-ha").

Blue Birds (0x10)
-----------------

0x100 - Input/cue
~~~~~~~~~~~~~~~~~
::

   0x100<type>

Cues an input according to ``type`` one beat later. Values for ``type`` are:

- 0: Peck (A press)

- 1: Stretch prepare (start holding A)

- 2: Stretch (stop holding A)

0x101 - Beat animation
~~~~~~~~~~~~~~~~~~~~~~
::

   0x101<pos>

Does the beat animation for the ``pos`` th character from the right, starting at 0.

0x102 - Captain Beak Movement
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x102

The captain moves his beak.

0x103-0x107 - Blue Birds Animations
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x103 part, pos

Does an animation depending on ``part`` for the ``pos`` th bird from the right, starting at 0. Values for ``part`` are:

- 0: Crouch down (part 1 of peck preparation)

- 1: Stretch up (part 2 of peck preparation) ::

   0x104<pos>

The ``pos`` th bird from the right pecks its beak once. ::

   0x105 part, pos

Does an animation depending on ``part`` for the ``pos`` th bird from the right, starting at 0. Values for ``part`` are:

- 0: Determined look (part 1 of stretch preparation)

- 1: Determined look with star (part 2 of stretch preparation) ::

   0x106<pos>

The ``pos`` th bird from the right starts charging up for a stretch. ::

   0x107<pos>

The ``pos`` th bird from the right stretches its neck.

0x108 - Unknown
~~~~~~~~~~~~~~~

An unknown operation ``0x108 0`` appears at the start of practices.

0x109 - Hide Captain
~~~~~~~~~~~~~~~~~~~~
::

   0x109

The captain goes off-screen.

0x10A - Show Memory
~~~~~~~~~~~~~~~~~~~
::

   0x10A<num>

Shows the ``num`` th memory, in the order they appear in the rhythm game Blue Birds, starting from 0.

0x10B - Hide Memory
~~~~~~~~~~~~~~~~~~~
::

   0x10B

The memory fades away.

0x10C - Fin.
~~~~~~~~~~~~
::

   0x10C

The text "Fin." appears.

List of subs
~~~~~~~~~~~~

All the following are asynchronous subroutines.

0x57
   A full "peck your beak" cue, such that the first input is two beats after the start.

0x58
   "Peck your beak" voice clip. (included in 0x57 and 0x61)

0x59
   Captain beak movement pattern for the "peck your beak" cue. (included in 0x57 and 0x61)

0x5A
   Relevant animations for the "peck your beak" cue. (included in 0x57)

0x5B
   A full "stretch out your neck" cue, such that the first input is 4 and a half beats after the start.

0x5C
   "Stretch out your neck" voice clip. (included in 0x5B and 0x5F)

0x5D
   Captain beak movement pattern for the "stretch out your neck" cue. (included in 0x5B and 0x5F)

0x5E
   Relevant animations for the "stretch out your neck" cue. (included in 0x5B)

0x5F
   A full "stretch out your neck" cue, where the first part of the preparation animation is skipped.

0x60
   Relevant animations for the "stretch out your neck" cue, except the first part of the preparation animation. (included in 0x5F)

0x61
   A full "peck your beak" cue, without the first part of the preparation animation.

0x62
   Relevant animations for the "peck your beak" cue, without the first part of the preparation animation. (included in 0x61)

The Dazzles (0x11)
------------------

0x100 - Input
~~~~~~~~~~~~~
::

   0x100 type, time

Cues up a button input depending on ``type`` after ``time`` ticks. Values for ``type`` are:

- 0: Stop holding A

- 1: Start holding A

0x101 - Enable/Disable Beat Animations
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x101 flag

Enables beat animations for all Dazzlers if ``flag`` is 1, disables if 0.

0x102 - Animations
~~~~~~~~~~~~~~~~~~
::

   0x102

Does the beat animation for all Dazzles. ::

   0x102<1> x, y

The ``y`` th Dazzle from the top on the ``x`` th row (both starting from 0) starts crouching. ::

   0x102<2> x, y

Unknown. Only used at the start of the game, and only on the player's Dazzle. ::

   0x102<3>

All Dazzles start charging up. ::

   0x102<4> x, y

The specified Dazzle poses. ::

   0x102<5>

All Dazzles stop posing.

0x103 - Charging
~~~~~~~~~~~~~~~~
::

   0x103 flag

Sets whether the Dazzles are charging for a pose (whether the player can pose). If ``flag`` is 1, enables, if 0, disables.

0x104 - Un-darken
~~~~~~~~~~~~~~~~~
::

   0x104 x, y

Un-darkens the specified Dazzle. Only used with the player's dazzle at the end of release cues.

0x105 - Player pose effect
~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x105 type

Sets which visual effect occurs when the player successfully poses for a cue. Values for ``type`` are:

- 0: Stars in a circle from the player's hand.

- 1: Stars with Play-yan.

0x106 - Shrink square
~~~~~~~~~~~~~~~~~~~~~
::

   0x106 x, y, time

The specified Dazzle's yellow square shrinks over ``time`` ticks.

List of subs
~~~~~~~~~~~~

All the following are asynchronous subroutines. Posing patterns are described in a 2x6 grid of numbers, the numbers
representing the order in which the Dazzles pose. Identical numbers are simultaneous.

0x56
   Countdown voice clips ("Three, two"), timed such that the first voice clip is four beats after the start.

0x57
   Dazzles start crouching from left to right. (no voice clips)

0x58
   Dazzles start crouching all at once. (unused; no voice clips)

0x59
   Dazzles start crouching from left to right 4 beats after start. (with voice clips)

0x5A
   Dazzles start crouching all at once 4 beats after start. (with voice clips)

0x5B
   Posing pattern::

      123
      123

   There is one beat between each pose.

0x5C
   Posing pattern::

      123
      123

   There is one beat between each pose, but the first pose only takes a half beat.

0x5D
   Posing pattern (unused)::

      145
      236

   There is a half beat between each pose.

0x5E
   Posing pattern (unused)::

      123
      456

   There is a half beat between each pose, with a beat delay between poses 3 and 4.

0x5F
   Posing pattern::

      153
      426

   There is a three-quarter beat between each pose, but only a half-beat between poses 3 and 4.

0x60
   Posing pattern::

      111
      222

   There are two beats between the poses.

Freeze Frame (0x12)
-------------------

0x100 - Input
~~~~~~~~~~~~~
::

   0x100 car, time

An input for a picture of car number ``car`` is cued for after ``time`` ticks.

0x101 - Beat animation
~~~~~~~~~~~~~~~~~~~~~~
::

   0x101 type

The beat animation is played for the photographer. The exact function of ``type`` is unknown, but beat animation calls
alternate between 1 and 0 for ``type``.

0x102 - Camera Overlay
~~~~~~~~~~~~~~~~~~~~~~
::

   0x102 flag

Enables the camera overlay if ``flag`` is 1, disables if 0.

0x103 - Car Control
~~~~~~~~~~~~~~~~~~~
::

   0x103 car, type

Sets car number ``car`` to a type determined by ``type``. Values for ``type`` are:

- 0: Yellow car

- 1: Red car ::

   0x103<1> car, x0, x1, time

Moves car number ``car`` in the background from ``x0`` to ``x1`` over ``time`` ticks. Positions are in units to the
right of the center (possibly pixels). ::

   0x103<2> car, x0, x1, time

Moves car number ``car`` in the foreground from ``x0`` to ``x1`` over ``time`` ticks.

0x104 - Performance Display
~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x104

Starts recording player performance for display. ::

   0x104<1> flag

Shows performance (thumbs up, side or down) if ``flag`` is 1, hides it if 0. ::

   0x104<2>

Sets the conditional variable to player performance (0 is thumbs up, 1 is side, 2 is down).

0x105 - Photograph Display
~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x105 car, flag

Shows the photograph for car number ``car`` if ``flag`` is 1, hides if 0.

0x106 - Audience
~~~~~~~~~~~~~~~~
::

   0x106 flag

Shows the audience if ``flag`` is 1, hides if 0.

0x107 - Stoplight Control
~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x107

Shows the stoplights. ::

   0x107<1>

Hides the stoplights. ::

   0x107<2> n

Turns on the ``n`` th stoplight, starting from 1.

0x108 - People in foreground
~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x108 num, type

A person walks across the foreground. ``num`` is the "slot" for the person, and people in higher slots appear behind
of people in lower slots. Used values for ``type`` are:

- 0: Man wearing white shirt walking left.

- 2: Woman walking left.

- 4: Man wearing blue shirt walking right.

List of subs
~~~~~~~~~~~~

All the following are asynchronous subroutines.

0x56
   One yellow car cue; photograph disappears quickly. (unused)

0x57
   One yellow car cue.

0x58
   Two yellow cars cue.

0x59
   Two yellow cars cue; photographs appear sequentially instead of simultaneously.

0x5A
   Three yellow cars cue.

0x5B
   Three yellow cars cue; photographs appear sequentially instead of simultaneously.

0x5C
   Red car cue; photograph disappears quickly.

0x5D
   Red car cue.

Glee Club (0x13)
----------------

0x100 - Input
~~~~~~~~~~~~~
::

   0x100 type

An input is cued for after one beat. Values of ``type`` are:

- 0: Release A (start singing)

- 1: Release A ("Together now")

- 2: Hold A (stop singing)

0x101 - Shut your Yap
~~~~~~~~~~~~~~~~~~~~~
::

   0x101<1>

Sets the conditional variable to 1 if you are holding A, 0 otherwise.

0x102 - Unknown
~~~~~~~~~~~~~~~
::

   0x102 ???

Unknown. ``0x102 0`` appears at the start of Glee Club and Glee Club 2, but not in remixes.

0x104 - Voice Control
~~~~~~~~~~~~~~~~~~~~~
::

   0x104 pos, type, sfx, ???, pitch

Sets the sound effect that plays when the ``pos`` th Chorus Kid from the left (starting at 0) performs an action depending
on ``type``. The sound effect that plays has SFX ID ``sfx`` and is pitch-shifted up by ``pitch`` (can be negative).
Note that the standard singing sound effect is ``0x1000DAF``, the sound effect to stop singing is ``0x1000DB1``, and
the sound effect for screaming is ``0x1000DB0``. Values for ``type`` are:

- 0: Singing

- 1: Screaming

- 2: Stop singing ::

   0x104<1> pos, type, ???

The ``pos`` th Chorus Kid performs an action depending on ``type``. Third argument is unknown, is usually 0. Values
for ``type`` are:

- 0: Singing

- 1: Screaming

- 2: Stop singing ::

   0x104<2> pos

Unknown.

0x105 - Conductor Animations
~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x105

Idle animation. ::

   0x105<1>

The conductor raises his baton. ::

   0x105<2>

The conductor snaps his baton.

0x106 - Unknown
~~~~~~~~~~~~~~~
::

   0x106

Often appears one beat after the player's Chorus Kid stops singing.

List of subs
~~~~~~~~~~~~
Only one relevant subroutine exists. It is synchronous.

0x56
   Shows the "shut your yap" message until the player shuts their yap.

Frog Hop (0x14)
---------------

0x100 - Input
~~~~~~~~~~~~~
::

   0x100 type, time

Cues an input depending on ``type`` for after ``time`` ticks. Values for ``type`` are:

- 0: Press A.

- 1: Start holding B.

- 2: Release B.

0x102 - Frog SFX
~~~~~~~~~~~~~~~~
::

   0x102 sfx

Plays the SFX with ID ``sfx`` (a voice clip), with the four dancer frogs "speaking" the SFX. Sound depends on player
performance.

0x103 - Frog Animations
~~~~~~~~~~~~~~~~~~~~~~~
For the purposes of all ``0x103`` operations, frog positions are as follows: The blue foreground frog is 0, the orange
foreground frog is 1, and the four background frogs are 2 through 5, from left to right. ::

   0x103 pos

Does the beat animation for the frog at position ``pos``. ::

   0x103<1> pos

The frog at position ``pos`` shakes their hips. ::

   0x103<2> pos

The frog at position ``pos`` does a long hip shake animation. ::

   0x103<3> pos

The frog at position ``pos`` does the first part of the spin animation. ::

   0x103<4> pos

The frog at position ``pos`` does the second part of the spin animation. ::

   0x103<5> pos, type

The frog at position ``pos`` moves their mouths depending on ``type``. Values of ``type`` are:

- 0: Open mouth wide briefly.

- 1: Open mouth narrowly.

- 2: Open mouth wide.

- 3: Open mouth wide horizontally.

List of subs
~~~~~~~~~~~~

Frog Hop has nearly 100 different useful subroutines. Some are synchronous, and some are asynchronous.

0x56
   Beat animation for all frogs. (sync)

0x57
   Hip shake for foreground frogs. (sync)

0x58
   Hip shake for background frogs. (sync)

0x59
   Long hip shake for foreground frogs. (sync)

0x5A
   Long hip shake for background frogs. (sync)

0x5B
   Spin part 1 for foreground frogs. (sync)

0x5C
   Spin part 1 for background frogs. (sync)

0x5D
   Spin part 2 for foreground frogs. (sync)

0x5E
   Spin part 2 for background frogs. (sync)

0x5F
   The spotlights change to the background frogs after 1 beat. Then change back to foreground
   frogs after 2 beats.   (async)

0x60
   Hip shake SFX. (sync)

0x61
   "Yeah" voice clip, along with mouth movement for the orange frog. (async)

0x62
   "Ya-" voice clip, along with mouth movement for the orange frog. (async)

0x63
   "-Hoo!" voice clip, along with mouth movement for the orange frog. (async)

0x64
   "-Hoo!" voice clip; mouth stays open longer. (async)

0x65
   "Yeah-yeah-yeah" voice clip, along with mouth movement for orange frog; mouth stays open throughout. (async)

0x66
   "Spin it" voice clip, along with speech bubble and mouth movement for orange frog. (async)

0x67
   "Boys!" voice clip, along with mouth movement for orange frog. (async)

0x68
   "Yeah" voice clip, along with mouth movement for background frogs. (async)

0x69
   "Ya-" voice clip, along with mouth movement for background frogs. (async)

0x6A
   "-Hoo!" voice clip, along with mouth movement for background frogs. (async)

0x6B
   "-Hoo!" voice clip; mouths stay open longer. (async)

0x6C
   "Yeah-yeah-yeah" voice clip, along with mouth movement for background frogs; mouths stay open throughout. (async)

0x6D
   "Spin it" voice clip, along with mouth movement for background frogs. (async)

0x6E
   "Spin it" voice clip; alternate mouth movement. (async)

0x6F
   "Boys!" voice clip, along with mouth movement for background frogs. (async)

0x70
   Four hip shake animations in a row for foreground frogs; uses sub 0x57. (async)

0x71
   Four hip shake animations in a row for background frogs; uses sub 0x58. (async)

0x72
   Four A press inputs in a row, starting one beat after the start of the sub. (async)

0x73
   Hip shake SFX four times in a row; uses sub 0x60. (async)

0x74
   Four full hip shake cues in a row; combines the previous four subs. (async)

0x75
   Two hip shake animations in a row for foreground frogs; uses sub 0x57. (async)

0x76
   Two hip shake animations in a row for background frogs; uses sub 0x58. (async)

0x77
   Two A press inputs in a row, starting one beat after the start of the sub. (async)

0x78
   Hip shake SFX two times in a row; uses sub 0x60. (async)

0x79
   Two full hip shake cues in a row; combines the previous four subs. (async)

0x7A
   A triple hip shake animation, for foreground frogs; uses subs 0x57 and 0x59. (async)

0x7B
   Two hip shake animations, followed by a triple hip shake animation, for background frogs; uses subs 0x58 and 0x5A. (async)

0x7C
   Two A press inputs one beat apart, followed by three A press inputs a half-beat apart, starting one beat after the
   start of the sub. Also includes player's "yeah-yeah-yeah" voice clips. (async)

0x7D
   "Yeah-yeah-yeah" voice clips for both the orange frog and background frogs; uses sub
   0x60, and subs 0x61 and 0x68. (async)

0x7E
   A full "yeah-yeah-yeah" cue; combines subs 0x7A through 0x7D, as well as 0x5F. (async)

0x7F
   "Yeah-yeah-yeah" voice clips for both the orange frog and background frogs; mouths stay open throughout. (async)

0x80
   A full "yeah-yeah-yeah" cue; combines subs 0x7A, 0x7B, 0x7C, 0x7F, and 0x5F (mouths stay open throughout). (sync; unused)

0x81-0x84
   Identical to 0x7A-0x7D, except there is a preceding hip shake. (async)

0x85
   A full "yeah-yeah-yeah" cue, with a preceding hip shake; combines the previous four subs, as well as 0x5F.

0x86
   Identical to 0x7F, except there is a preceding hip shake. (async)

0x87
   Identical to 0x80, except there is a preceding hip shake. (async)

0x88-0x8E
   Identical to 0x7A-0x80, except there is no preceding hip shake. (async)
   
0x8F
   One hip shake animation, followed by a "ya-hoo!" pattern animation, for foreground frogs; uses subs 0x57 and 0x59. (async)
   
0x90
   Three hip shake animations, followed by a "ya-hoo!" pattern animation, for background frogs; uses subs 0x58 and 0x5A. (async)
   
0x91
   Three A press inputs one beat apart, followed by A press inputs in a "ya-hoo!" pattern (two a half-beat apart), starting
   one beat after the start of the sub. Also includes the player's "Ya-hoo!" voice clips. (async)
   
0x92
   One hip shake SFX, followed by "Ya-hoo!" voice clips for both the orange frog and background frogs; uses sub 0x60,
   and subs 0x62, 0x63, 0x69, and 0x6A. (async)
   
0x93
   A full "Ya-hoo!" cue; combines sub 0x8F-0x92, as well as 0x5F. (async)
   
0x94
   One hip shake SFX, followed by "Ya-hoo!" voice clips for both the orange frog and background frogs. Mouths stay
   open longer; uses sub 0x60, and subs 0x62, 0x64, 0x69, and 0x6B. (async)
   
0x95
   Identical to 0x7F, except there is a 1 beat delay. (async)
   A full "Ya-hoo!" cue. Mouths stay open longer; combines subs 0x8F-0x91, 0x94, and 0x5F. (async)
   
0x96-0x99
   Identical to 0x88-0x8B, except there is no preceding hip shake. (async)
   
0x9A
   A full "Ya-hoo!" cue without preceding hip shake; combines the previous four subs, as well as 0x5F. (async)

0x9B
   Identical to 0x8C, except there is no preceding hip shake. (async)

0x9C
   A full "Ya-hoo!" cue without preceding hip shake. Mouths stay open longer; combines subs 0x8F-0x91, 0x94, and 0x5F. (async)

0x9D
   Three hip shake animations in a row for foreground frogs, starting one beat after the start of the sub. Uses sub 0x57. (async)

0x9E
   Three hip shake animations in a row for background frogs, starting one beat after the start of the sub. Uses sub 0x58. (async)

0x9F
   Three A press inputs in a row one beat apart, starting two beats after the start of the sub. (async)

0xA0
   Three hip shake sound effects in a row, starting one beat after the start of the sub. Uses sub 0x60. (async)

0xA1
   Three full hip shake cues, starting two beats after the start of the sub; combines the previous four subs. (async)

0xA2-0xA6
   Identical to 0x96-0x9A, except there is only one hip shake instead of three. (async)

0xA7
   A full spin animation for foreground frogs. Uses subs 0x5B and 0x5D. (async)

0xA8
   Two hip shake animations, followed by a full spin animation for background frogs. Uses subs 0x5C and 0x5E. (async)

0xA9
   Two A press inputs one beat after the start of the sub, followed by a B hold-and-release. Also includes
   player's "Spin it, boys!" voice clips. (async)

0xAA
   "Spin it, boys!" voice clips for both the orange frog and background frogs; uses subs 0x66, 0x67, 0x6D and 0x6F. (async)

0xAB
   A full "Spin it, boys!" cue; combines the previous four subs, as well as 0x5F. (async)

0xAC
   "Spin it, boys!" voice clips for both the orange frog and background frogs; alternate mouth movement for background frogs.
   Uses subs 0x66, 0x67, 0x6D and 0x6E. (async)

0xAD
   A full "Spin it, boys!" cue; combines subs 0xA0-0xA3, 0xA5 and 0x5F. (sub; unused)

0xAE-0xB1
   Identical to 0xA0-0xA3, except there is only one preceding hip shake. (async)

0xB2
   A full "Spin it, boys!" cue with only one preceding hip shake; combines the previous four subs, as well as 0x5F. (async)

0xB3
   Identical to 0xA5. (async)

0xB4
   Identical to 0xA6, but with only one preceding hip shake. Combines subs 0xA7-0xA9, 0xAC and 0x5F. (async)

0xB5
   "One, two, three, four!" count-in with mouth movements for the orange frog. (async)

Fan Club (0x15)
---------------

0x100 - Input
~~~~~~~~~~~~~
::

   0x100<type>

Cues an input for one half-beat after the operation, depending on ``type``. Values for ``type`` are:

- 0: A press. (can be omitted)

- 1: Start holding A.

- 2: Release A.

0x101-0x106 - Monkey Animations
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x101<1>

Beat animation for all monkeys. ::

   0x102<1>

All monkeys put their hands up to clap. ::

   0x102<2>

All monkeys put their hands up to clap while shaking. ::

   0x103 pos

One monkey claps depending on ``pos``. ::

   0x103<1>

All monkeys clap. ::

   0x104 pos

One monkey stops clapping depending on ``pos``. ::

   0x104<1>

All monkeys stop clapping. ::

   0x105<1>

All monkeys start charging for a jump. ::

   0x106<1>

All monkeys jump.

0x107-0x111 - Performer Animations
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
For the purposes of all operations in this category, performer indices are as follows: The singer is 0, left backup
is 1, right backup is 2.
::

   0x107 i

Beat animation for the performer at index ``i``. ::

   0x108 i

Beat animation with one hand raised. ::

   0x109 flag, i

Hand twirl animation. If ``flag`` is 1, adds stars. ::

   0x10A i

Clap animation. ::

   0x10B i

Stretch animation. ::

   0x10C i

Jump animation. ::

   0x10D i

After-jump animation. ::

   0x10E

Singer does "I suppose" animation. ::

   0x10F

Singer does beat animation after "I suppose". ::

   0x110 i, dist, type

The performer at index ``i`` slides ``dist`` units to one direction, doing one of two animations determined by
``type`` (0 or 1). Never used for singer. ::

   0x111 flag, i

Sets the performer at ``i`` to visible if ``flag`` is 1, invisible if 0.

0x112 - Lights
~~~~~~~~~~~~~~
::

   0x112

The lights flash. ::

   0x112<1> flag

The stars (in Fan Club 2) start flashing if ``flag`` is 1, stop if 0. ::

   0x112<2>

The lights (in Fan Club 2) flash in the first color scheme. ::

   0x112<3>

The lights (in Fan Club 2) flash in the second color scheme.

0x114 - Background control
~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x114 flag

Background darkens if ``flag`` is 1, lightens if 0.

0x116 - Confetti
~~~~~~~~~~~~~~~~
::

   0x116

Confetti pops out.

List of subs
~~~~~~~~~~~~
All the following are asynchronous subroutines.

0x57
   A full 4-clap cue, including animations and inputs.

0x58
   A full "I suppose" cue, including animations and inputs.

0x59
   A full "I suppose" cue; different monkey voice clip.

0x5A
   A full "I suppose" cue; different monkey voice clip.

0x5B
   A full "I suppose" cue; different monkey voice clip.

0x5C
   A full "I suppose" cue; different monkey voice clip.

0x5D
   A full "oh" cue, including animations, inputs, and voice clip.

0x5E
   Inputs for a 4-clap cue, starting one beat after the start of the sub.

0x5F
   Inputs for an "I suppose" cue.

0x60
   Inputs for an "oh" cue.

0x61
   Monkeys start clapping forever one after another (end of Fan Club 2).

Dog Ninja (0x16)
----------------

0x100 - Input/Throw Objects
~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x100 time, obj1, obj2

Spawns objects, to be cut after ``time`` ticks. ``obj1`` is the object coming from the right, ``obj2`` from the left.
Values for ``obj1`` and ``obj2`` are:

- 0: Cucumber.

- 1: Broccoli.

- 2: Apple.

- 3: Carrot.

- 4: Potato.

- 5: Bone.

- 6: Yellow pepper.

- 7: Tire.

- 8: Frying pan.

- 9: Game crash!

- 0xA: Nothing.

0x101 - Beat Animation
~~~~~~~~~~~~~~~~~~~~~~
::

   0x101

Beat animation.

0x102 - Cut Everything!
~~~~~~~~~~~~~~~~~~~~~~~
::

   0x102 flag

Shows the bird holding a sign saying "cut everything" if ``flag`` is 1, hides if 0.

Rhythm Rally (0x17)
-------------------

Note: Units for arc height seem to be arbitrary. Arc height of a standard 1-beat bounce is 0x3E8 (1000), that of a 2-beat
bounce is 0xBB8 (3000), and that of a half-beat bounce is 0x29E (670).

0x100 - Input
~~~~~~~~~~~~~
::

   0x100 ???, height, time

The player will have an input after ``time`` ticks, after which the ball will bounce to the other side of the table
over ``time`` ticks with an arc height of ``height``. The first argument is 1 in a turbo rally, and 0 otherwise.

0x101 - Bounce Ball
~~~~~~~~~~~~~~~~~~~
::

   0x101 type, height, time

The ball will bounce to a point determined by ``type`` over ``time`` ticks, with an arc height of ``height``.
Values for ``type`` are:

- 1: The opponent's paddle.

- 2: The player's side of the table.

- 3: The player's paddle.

- 5: The opponent's hand. (when serving)

0x102 - Player Performance
~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x102

Sets the conditional variable to 1 if the player failed an input since the last ``0x102<1>``, and 0 otherwise.

0x104-0x107 - Paddler Animations
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x104 i

A paddler corresponding to ``i`` does a beat animation. 0 is the opponent, 1 is the player. ::

   0x105 i

A paddler corresponding to ``i`` readies themselves to hit the ball. ::

   0x106 i

A paddler corresponding to ``i`` hits the ball. ::

   0x107 i

A paddler corresponding to ``i`` throws their arms up in victory.

List of subs
~~~~~~~~~~~~

All the following are asynchronous subroutines.

0x56
   A single regular, 1-beat rally.

0x57
   A single regular, 2-beat rally.

0x58
   A single regular, half-beat rally. (turbo rally)

0x59
   A single half-beat rally, where the player hits the ball as though it were a 1-beat rally. (fast rally)

0x5A
   Fast rally cue sound, two beats after the start of the sub.

0x5B
   Turbo rally cue sound.

Fillbots (0x18)
---------------

0x100 - Input/Fill Robot
~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x100 len

Sets up a full robot cue, including the inputs for holding and releasing A, 4 beats after the operation. Values for ``len`` are:

- 0: Small Fillbot

- 4: Normal Fillbot

- 7: Large Fillbot

Note that, while these values seem to suggest some correlation to the amount of beats A needs to be held, the values
between these result in, while strictly increasing, odd non-linearly increasing times.

0x101 - Nozzle Control
~~~~~~~~~~~~~~~~~~~~~~
::

   0x101

Beat animation for nozzle. ::

   0x101<2> type

Sets how low the nozzle goes if you press A. If ``type`` is 1 it will go down to small fillbot level, otherwise normal level.

0x102 - Robot Animations
~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x102 type, end

Assembles a robot of type ``type`` after half a beat. If ``end`` is 1, the robot will go off-screen after the cue, if 0, it will stay
in place. Values for ``type`` are:

- 0: Normal fillbot.

- 1: Large fillbot.

- 2: Small fillbot. ::

   0x102<1> type, end, time

Same as the previous, but the robot gets assembled after ``time`` ticks instead of a half beat. ::

   0x102<3>

Beat animation for robot.

0x106 - Factory Screen?
~~~~~~~~~~~~~~~~~~~~~~~
::

   0x106 bflyt

Takes the name of a ``.bflyt`` file and shows the factory screen (?).

List of subs
~~~~~~~~~~~~
All the following are asynchronous subroutines.

0x5D
   Beat animations every beat for 4 beats.

0x5E
   Beat animations every beat for 8 beats.

0x5F
   Beat animations every beat for 16 beats.

0x60
   Beat animations every beat for 32 beats.

0x61
   Beat animations every beat for 64 beats.

0x62
   Beat animations every beat for 128 beats.

0x63
   Beat animations every beat, forever.

0x64-0x6B
   Robot cues with ``0x100`` values 0-7 in order, all stay stationary after cue. 0, 4, and 7 (0x64, 0x68, 0x6B) are
   their respective robot types, others are normal fillbots graphically.

0x6C-0x73
   Same as previous 8, but they go off-screen after cue. Of note are 0x6C, 0x70, 0x73, the used patterns.

Shoot-'Em-Up (0x19)
-------------------

0x100 - Spawn Alien
~~~~~~~~~~~~~~~~~~~
::

    0x100 pos, time

Spawns an alien at a position determined by ``pos``, to be shot after ``time`` ticks. Values for ``pos`` are:

- 0: Far top left corner.

- 1: Far top right corner.

- 2: Slightly down and right from 0.

- 3: Slightly down and left from 1.

- 4: Slightly down and right from 2.

- 5: Slightly down and left from 3.

- 6: Slightly to the left of the center.

- 7: Slightly to the right of the center.

- 8: In the center.

- 9: Nearly identical to 6.

- 0xA: Nearly identical to 7.

- 0xB: Left and slightly down from the center.

- 0xC: Right and slightly down from the center.

- 0xD: Down and left from 0xB.

- 0xE: Down and right from 0xC.

- 0xF: Slightly down and left from 0xD. (far bottom left corner)

- 0x10: Slightly down and right from 0xE. (far bottom right corner)

0x102 - Metal Doors
~~~~~~~~~~~~~~~~~~~
::

    0x102 1

Spawns metal doors for the opening of Shoot-'Em-Up 1. ::

    0x102<n>

Reveals part of the game, removing one of the three doors. ``n`` is which door to open, starting from 0, in the order
they are opened in Shoot-'Em-Up 1.

0x103 - Screen Control
~~~~~~~~~~~~~~~~~~~~~~
::

    0x103

The screen that looks suspiciously like a Wii U GamePad is brought into view. ::

    0x103<1>

The screen is hidden.

0x104 - Radio Lady Control
~~~~~~~~~~~~~~~~~~~~~~~~~~
::

    0x104

The radio lady pops up on the screen. ::

    0x104<1>

The radio lady is removed from view. ::

    0x104<2>

Beat animation for radio lady. ::

    0x104<3> num

The radio lady opens her mouth ``num`` times, simulating speech.

List of subs
~~~~~~~~~~~~
All the following are asynchronous subroutines.

0x56-0x66
    An alien is spawned at position 0-0x10 respectively a half beat later, along with a sound effect, to be shot 4 beats
    after it appears.

0x67-0x77
    An alien is spawned at position 0-0x10 respectively a half beat later, along with a sound effect, to be shot 8 beats
    after it appears.

Big Rock Finish (0x1A)
----------------------

0x100 - Input
~~~~~~~~~~~~~
::

   0x100 time, type

Cues up an A press input after ``time`` ticks. If ``type`` is 1, the player will also jump up (final strum in a cue).

0x102 - Guitar Sound Control
~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x102 short, long

Sets the length of a short and long guitar strum sound in ticks. ::

   0x102<1> pitch

Sets the pitch relative to the base guitar sound in semitones.

0x103 - Light Patterns
~~~~~~~~~~~~~~~~~~~~~~
::

   0x103 type

Causes a light overlay effect to occur. Values for ``type`` are:

- 0: Nothing.

- 1: Green swirl pattern. (as in BRF Patterns A, D, G)

- 2: Blue dot pattern. (as in BRF Patterns B, E, H)

- 3: Red pattern. (as in BRF Patterns C, F)

- 4: White flash. (when strumming)

0x104 - Player Animations
~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x104

Player beat animation. ::

   0x104<1>

Player ready animation.

0x105 - Green Ghost Animations
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x105

Green ghost beat animation. ::

   0x105<1>

Green ghost ready animation. ::

   0x105<3>

Green ghost strum animation. ::

   0x105<4>

Green ghost long strum animation.

0x106 - Drummer Animations
~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x106

Bass drum animation. ::

   0x106<1>

Snare drum animation. ::

   0x106<2>

Hi-hat animation. ::

   0x106<3>

Cymbal animation. ::

   0x106<4>

Right tom animation. ::

   0x106<5>

Left tom animation.

List of subs
~~~~~~~~~~~~
All the following are asynchronous subroutines unless noted otherwise.

0x57
   Player and green ghost animations for a regular guitar cue.

0x58
   Drummer animations for a regular guitar cue.

0x59
   Audience animations for a regular guitar cue.

0x5A
   Inputs for a regular guitar cue.

0x5B
   A full regular guitar cue.

0x5C
   Player and green ghost animations for a swingy guitar cue.

0x5D
   Drummer animations for a swingy guitar cue.

0x5E
   Audience animations for a swingy guitar cue.

0x5F
   Inputs for a swingy guitar cue.

0x60
   A full swingy guitar cue.

0x61
   Player and green ghost animations for a double-speed guitar cue.

0x62
   Drummer animations for a double-speed guitar cue.

0x63
   Audience animations for a double-speed guitar cue.

0x64
   Inputs for a double-speed guitar cue.

0x65
   A full double-speed guitar cue.

0x66 (synchronous)
   Beat animations for the player, green ghost and audience.

0x67
   Four beat animations for the player, green ghost and audience.

0x68
   "Thank you" voice clip.

0x6B-0x72
   Drum patterns for songs A-H respectively.

Munchy Monk (0x1B)
------------------

0x100 - Input
~~~~~~~~~~~~~
::

   0x100 type

Sets an input. Values for ``type`` are:

- 0: "One" dumpling, input is after 1 beat.

- 1: Second dumpling in "Two" cue; input after 1.5 beats.

- 2: Second and third dumplings in "Three" cue; input after a half beat.

0x102 - Spawn Dumpling
~~~~~~~~~~~~~~~~~~~~~~
::

   0x102 color

A dumpling is spawned into the player's hand. Up to two can be stacked; stacking more than two will crash the game.
Values for ``color`` are:

- 0: White.

- 1: Pink.

- 2: Green.

0x103 - Hand Control
~~~~~~~~~~~~~~~~~~~~
::

   0x103 type

A hand is spawned to give the player dumplings. Values for ``type`` are:

- 0: Hand used in "one" cue.

- 1: Hand used in "two" cue.

- 2: Hand used in "three" cue.

::

   0x103<1>

Removes the hand.

0x104 - Slide Forward
~~~~~~~~~~~~~~~~~~~~~
::

   0x104

The Monk slides forward to reveal a baby.

0x106 - Hands Forward
~~~~~~~~~~~~~~~~~~~~~
::

   0x106

The Monk's hands slide forward.

0x107 - Background Movement
~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x107 flag, ???

The background starts moving if ``flag`` is 1. The second argument is unknown, possibly speed or time it takes to start
moving at full speed.

List of subs
~~~~~~~~~~~~

All the following are asynchronous subroutines. All are such that the first dumpling will be placed into the
Monk's hand one beat after the start of the sub.

0x56
   "One, go!" cue.

0x57
   "T-Two, go-go!" cue.

0x58
   "Three, go go go!" cue.

Built to Scale (0x1C)
---------------------

0x101 - Paddle Control
~~~~~~~~~~~~~~~~~~~~~~
Note that when a paddle number is an argument, it's the nth paddle from the left, starting at 0. -1 is off-screen to the left
and to the right.

::

   0x101 p1, p2

Used when a peg bounces from paddle ``p1`` to ``p2``. Does the hitting animation for the paddle. ::

   0x101<1>

Paddles reset to their neutral state. ::

   0x101<2> p

Paddle ``p`` charges up. ::

   0x101<3> p

Paddle ``p`` stops charging up.

0x102 - Peg Control
~~~~~~~~~~~~~~~~~~~
::

   0x102 p1, p2, ???, cflag, pflag

The peg bounces from ``p1`` to ``p2`` over one beat. If ``p2`` is the player's paddle, includes an input. The third
argument is always 0. If ``cflag`` is 1, it indicates that the paddle at the destination is charged up and will shoot
the peg. If ``pflag`` is 1, it indicates that a charge will occur on the next bounce. ::

   0x102<1> p

Used on the beat the peg will be shot by paddle ``p``. Presumably causes the peg to fall if the player missed.

0x103 - Widget Pair Control
~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x103 slot, active

Sets widget pair slot ``slot`` to active (visible?) if ``active`` is 1, inactive if 0. ::

   0x103<1> slot, i, pos

Moves widget half ``i`` (0 is closer to paddles, 1 is further away) of pair ``slot`` to position ``pos``. Position 0 is
the middle, negative is to the left, positive is to the right. ::

   0x103<2> slot

Combines widget in ``slot`` if the player shoots them correctly the next beat. ::

   0x103<4>

Sets the conditional variable to the number of slots which are active.

List of subs
~~~~~~~~~~~~

All the following are asynchronous subroutines. (???) indicates a sub that doesn't make sense within
the rules of the rhythm game.

0x56
   The charging sound.

0x57
   Bounce from paddle 1 to 2 to 3 to off-screen.

0x58
   Bounce from off-screen to 3 to 2 to 1 to 0.

0x59
   Bounce from 0 to 1 to 2 to 3 to 2. (unused)

0x5A
   Bounce from 2 to 1 to 0 to 1 to 2. (unused)

0x5B
   Bounce from 2 to 3 to 2 to 1 to 0. (unused)

0x5C
   Bounce from 3 to 2 to 1 to 0 to 1.

0x5D
   Bounce from 1 to 2 to 3 to 2 to 1. (unused)

0x5E
   Bounce from 1 to 0 to 1 to 2 to 3. (unused)

0x5F
   Bounce from 0 to 1 to 2 to 3, then shoot with 2.

0x60
   Bounce from 2 to 1 to 0 to 1, then shoot with 2. (unused)

0x61
   Bounce from 2 to 3 to 2 to 1, then shoot with 0. (???, unused)

0x62
   Bounce from 3 to 2 to 1 to 0, then shoot with 1. (???, unused)

0x63
   Bounce from 1 to 2 to 3 to 2, then shoot with 1. (???, unused)

0x64
   Bounce from 1 to 0 to 1 to 2, then shoot with 3. (???, unused)

0x65
   Bounce from 0 to 1 to 2 to 3, then start preparing for a charge at 2. (???, unused)

0x66
   Bounce from 2 to 1 to 0 to 1, then start preparing for a charge at 2. (???, unused)

0x67
   Bounce from 2 to 3 to 2 to 1, then start preparing for a charge at 0. (???, unused)

0x68
   Bounce from 3 to 2 to 1 to 0, then start preparing for a charge at 1. (unused)

0x69
   Bounce from 1 to 2 to 3 to 2, then start preparing for a charge at 1. (???, unused)

0x6A
   Bounce from 1 to 0 to 1 to 2, then start preparing for a charge at 3. (unused)

0x6B
   Paddle 0 stops charging, and the peg falls if not hit. (???, unused)

0x6C
   Paddle 2 stops charging, and the peg falls if not hit.

0x6D
   Paddle 3 stops charging, and the peg falls if not hit. (???, unused)

0x6E
   Paddle 1 stops charging, and the peg falls if not hit. (???, unused)

0x6F
   Bounce from 0 to 1 to shoot with 1. (???, unused)

0x70
   Bounce from 2 to 1 to shoot with 1. (???, unused)

0x71
   Bounce from 2 to 3 to shoot with 3. (???, unused)

0x72
   Bounce from 3 to 2 to shoot with 2. (unused)

0x73
   Bounce from 1 to 2 to shoot with 2. (unused)

0x74
   Bounce from 1 to 0 to shoot with 0. (???, unused)

0x75
   Bounce from 0 to 1, then shoot with 2.

0x76
   Bounce from 2 to 1, then shoot with 0. (???, unused)

0x77
   Bounce from 2 to 3, then shoot with 2. (unused)

0x78
   Bounce from 3 to 2, then shoot with 1. (???, unused)

0x79
   Bounce from 1 to 2, then shoot with 3. (???, unused)

0x7A
   Bounce from 1 to 0, then shoot with 1. (???, unused)

0x7B
   Bounce from 0 to 1 to 2, then shoot with 3. (???, unused)

0x7C
   Bounce from 2 to 1 to 0, then shoot with 1. (???, unused)

0x7D
   Bounce from 2 to 3 to 2, then shoot with 1. (???, unused)

0x7E
   Bounce from 3 to 2 to 1, then shoot with 0. (???, unused)

0x7F
   Bounce from 1 to 2 to 3, then shoot with 2.

0x80
   Bounce from 1 to 0 to 1, then shoot with 2. (unused)

0x81-0x84
   Spawn widget halves in slots 0-3 respectively, starting 6 beats away from the middle, and move them every beat.

0x85-0x88
   Spawn widget halves in slots 0-3 respectively, starting 5 beats away from the middle, and move them every beat. (unused)

0x89-0x8C
   Spawn widget halves in slots 0-3 respectively, starting 4 beats away from the middle, and move them every beat. (unused)

0x8D-0x90
   Spawn widget halves in slots 0-3 respectively, starting 3 beats away from the middle, and move them every beat. (unused)

0x91-0x94
   Spawn widget halves in slots 0-3 respectively, starting 2 beats away from the middle, and move them every beat. (unused)

0x95-0x98
   Spawn widget halves in slots 0-3 respectively, starting at the middle, and move them every beat. (unused)

0x99-0x9C
   Identical to 0x81-0x84, but the widget halves move every half beat. (unused)

0x9D-0xA0
   Identical to 0x81-0x84, but the widget halves move every quarter beat. (unused)

0xA1-0xA4
   Identical to 0x81-0x84, but the widget halves move every eighth beat. (unused)

0xA5
   0x58 followed by 0x75 (off-screen -> 3 -> 2 -> 1 -> 0 -> 1 -> shoot)

0xA6
   0x5C followed by 0x7F (3 -> 2 -> 1 -> 0 -> 1 -> 2 -> 3 -> shoot), then
   from off-screen into 0x5F (off-screen -> 0 -> 1 -> 2 -> 3 -> shoot), then
   spawn a peg from off-screen onto paddle 3. (this is the entire pattern that is repeated in Built to Scale.)

0xA7
   Identical to 0xA6, but without spawning the next peg.

Air Rally (0x1D)
----------------

0x100 - Input
~~~~~~~~~~~~~
::

    0x100

Cues an A press input. Time depends on arc time.

0x101 - Plane Positions
~~~~~~~~~~~~~~~~~~~~~~~
::

    0x101 pos, flag

The player's plane's position becomes ``pos``. If ``flag`` is 1, this will be instant, otherwise it'll be smooth.
Known values for ``pos`` are:

- 0: Behind camera.

- 1: Regular position.

::

    0x101<1> pos, flag

Forthington's plane's position becomes ``pos``. If ``flag`` is 1, this will be instant, otherwise smooth. Known values
for ``pos`` are:

- 0: Behind camera.

- 2: Regular position.

- 3: Further away from camera.

- 4: Yet further away from camera.

- 5: Very far away from camera.

0x102 - Shuttle Respawn
~~~~~~~~~~~~~~~~~~~~~~~
::

    0x102

Respawns a shuttle at Forthington's racket if an input was missed.

0x103 - Hit Shuttle
~~~~~~~~~~~~~~~~~~~
::

    0x103

Forthington hits the shuttle to Baxter.

0x104 & 0x105 - Catch Shuttle
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

    0x104
    0x105

Forthington catches the shuttle. Seems to work only when both are present.

0x107 - Cloud Speed
~~~~~~~~~~~~~~~~~~~
::

    0x107<speed>

Sets the speed of clouds rushing by, as a percentage of the regular speed in Air Rally 1.

0x109 - Color Overlay
~~~~~~~~~~~~~~~~~~~~~
::

    0x109<type> r, g, b

Sets the color overlay for an element determined by ``type`` to the color determined by the given RGB values. Values
for ``type`` are:

- 0: Background

- 1: Clouds

- 2: Baxter, Forthington, and the shuttle

- 3: Unknown

0x10A - Colors over Time
~~~~~~~~~~~~~~~~~~~~~~~~
::

    0x10A<time>

Applies the most recent ``0x109`` changes to every type of element over ``time`` ticks.

0x10B - Set Arc Time
~~~~~~~~~~~~~~~~~~~~
::

    0x10B time1, time2

Sets the time for the shuttle to go from Forthington to Baxter to ``time1`` and the time for the shuttle to go from
Baxter to Forthington to ``time2``. Affects inputs.

0x10E - Forthington Mouth Movement
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

    0x10E<2>

Forthington moves his mouth.

List of subs
~~~~~~~~~~~~

All the following are asynchronous unless otherwise noted.

0x56
    Forthington speaks for a while.

0x57
    Input and animation for a regular hit.

0x58
    Input and animation for two regular hits.

0x59
    Input and animation for a high hit.

0x5A
    Mouth movement for "ba-bum bum bum"

0x5B
    Identical to 0x5A? SFX added?

0x5C
    Mouth movement for "Two three four!"

0x5D
    Identical to 0x5C? SFX added?

0x5E
    Identical to 0x57.

0x5F
    A regular hit with "ba-bum bum bum"

0x60
    A high hit with "ba-bum bum bum"

0x61
    A high hit with "Two three four!"

0x62
    A high hit.

0x63
    A high hit that doesn't respawn the shuttle.

0x64
    A high hit that doesn't respawn the shuttle. The shuttle returns to Forthington in one beat instead of two.

0x65
    Removes any color overlay (makes it white). (sync)

0x66
    Adds an "evening" overlay (orange; characters are black). (sync)

0x67
    Adds a dark overlay. (sync)

0x68
    "evening" overlay, but without characters being silhouettes. (sync)

Exhibition Match (0x1E)
-----------------------

0x100 - Spawn Ball/Input
~~~~~~~~~~~~~~~~~~~~~~~~
::

    0x100 time

Spawns a ball to be hit after ``time`` ticks. ``time`` is usually ``0x20`` due to the timing of the monkey's throw animation.

0x101 - Slugger Animations
~~~~~~~~~~~~~~~~~~~~~~~~~~
::

    0x101

Beat animation for slugger. ::

    0x101<1>

Slugger tenses up in preparation for a hit. ::

    0x101<2> type

Post-pitch animation. Values for ``type`` are:

- 0: Look at the camera.

- 1: Face the camera. (This is probably not intended for mid-pitch)

- 2: Sniff.

0x102 - Pitcher Animations
~~~~~~~~~~~~~~~~~~~~~~~~~~
::

    0x102

Beat animation for pitcher. ::

    0x102<1>

Wind-up animation. ::

    0x102<2>

Throw animation.

0x103 - Monkey Animations
~~~~~~~~~~~~~~~~~~~~~~~~~
::

    0x103

Beat animation for monkey. ::

    0x103<1>

Throw animation.

0x104 - Curtain
~~~~~~~~~~~~~~~
::

    0x104<2>

Hide curtain.

0x105 - Slugger Swinging Animation
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

    0x105 type

Sets the slugger's swinging animation. Values for ``type`` are:

- 0: Default swinging animation.

- 1: Spinning animation.

0x108 - Zoom
~~~~~~~~~~~~
::

    0x108 zoom, time

Sets the zoom to ``zoom`` over ``time`` ticks. Default zoom value is -64. ::

    0x108<1> zoom, time

Adds ``zoom`` to the zoom level over ``time`` ticks.

0x10B - Flash Lights
~~~~~~~~~~~~~~~~~~~~
::

    0x10B

Flashes the lights (only visible when very zoomed out; most zoomed-out view used is 0x139C).

List of subs
~~~~~~~~~~~~

All the following are asynchronous.

0x56
    Four beat animations in a row for all characters.

0x57
    Two beat animations, followed by a pitching animation.

0x58
    Regular throwing cue, from the moment the pitcher throws.

0x59
    Same as 0x58, but the slugger looks at the camera mid-pitch.

0x5A
    Same as 0x58, but the slugger faces the camera mid-pitch.

0x5B
    Same as 0x58, but the slugger sniffs mid-pitch.

0x5C
    Nothing at all.

0x5D
    Unknown.

0x5E
    Waits 8 beats, then zooms camera in 4 times before resetting the zoom.

0x5F
    Waits 8 beats, then zooms camera in 3 times before resetting the zoom.

0x60
    Waits 8 beats, then zooms camera in slowly before resetting the zoom.

0x61
    Waits 8 beats, then zooms camera in twice before resetting the zoom.

0x62
    Waits 8 beats, then zooms camera in sharply once, then slowly, before resetting the zoom.

0x63
    Waits 8 beats, then zooms camera out very far before resetting the zoom.

0x64
    Identical to 0x5E, the zoom is slightly more intense.

0x65
    Identical to 0x5F, the zoom is slightly more intense.

0x66
    Identical to 0x60, the zoom is slightly more intense.

0x67
    Identical to 0x61, the zoom is slightly more intense.

0x68
    Identical to 0x62, the zoom is slightly more intense.

Flockstep (0x1F)
----------------

0x100 - Input
~~~~~~~~~~~~~
::

   0x100 type, time

Cues up an input based on ``type`` in ``time`` ticks. Values for ``type`` are:

- 0: Press A.

- 1: Start holding B.

- 2: Stop holding B.

- 4: Stop holding A. (When the other birds lift their feet; input not mandatory, but includes bird movement.)

0x102 - Bird Animations
~~~~~~~~~~~~~~~~~~~~~~~
::

   0x102 ???, type

Does an animation for all birds. First argument is unknown, but seems to be 5 often. Values for ``type`` are:

- 0: Step.

- 1: Lift leg.

::

   0x102<1> ???

All birds charge. Argument seems to always be 5. ::

   0x102<2> ???

All birds jump. Argument seems to always be 5. ::

   0x102<3> num

Set the amount of birds besides the four main ones. Birds will fly away or land to match the number specified. ::

   0x102<5> num

``num`` new birds land.

0x104 - Beat Animation
~~~~~~~~~~~~~~~~~~~~~~
::

   0x104 0

Beat animation for all birds.

0x107 - Color Variety
~~~~~~~~~~~~~~~~~~~~~
::

   0x107 flag

Newly added birds can be only pink if ``flag`` is 0, all colors if 1.

0x108 - Earth Animation
~~~~~~~~~~~~~~~~~~~~~~~
::

   0x108 view

Changes the view to one of the views from the Earth animations in Flockstep. Values for ``view`` are 0 through 5,
and are in the order they appear in the game. ::

   0x108<1>

Resets the view to normal.

0x109 - Earth In Eye
~~~~~~~~~~~~~~~~~~~~
::

   0x109

An image of the Earth appears in the eye of the player's bird. ::

   0x109<1>

Removes the image.

List of subs
~~~~~~~~~~~~
All the following are asynchronous.

0x56
   Count-in with cowbells.

0x57
   Identical to 0x56.

0x58
   Inputs for a step cue.

0x59
   Inputs to start charging.

0x5A
   Inputs to jump.

0x5B
   Additional animations and camera movement for a step cue.

0x5C
   Additional animations and camera movement for a charge cue.

0x5D
   Additional animations and camera movement for a jump cue.

0x5E
   A full step cue.

0x5F
   A full charge cue.

0x60
   A full jump cue.

Cheer Readers (0x20)
--------------------

0x100 - Input
~~~~~~~~~~~~~
::

   0x100 type, time, effect

Sets up a cue depending on ``type`` for after ``time`` ticks. ``effect`` adds some effects when the cue is hit successfully.
Values for ``type`` are:

- 2: Start holding B.

- 3: Release B.

- 5: Press A.

Values for ``effect`` are:

- 0: Nothing.

- 1: Books gleam and the player's cheer reader says "Yay!".

- 3: Books gleam and all cheer readers say "Yay!".

- 4: Special sound effect. Found in the "Rah-rah sis boom bah-BOOM!" cue.

0x101 - Flip Book
~~~~~~~~~~~~~~~~~
::

   0x101 x, y

The cheer reader in the ``x`` th column and the ``y`` th row (from the top left, starting at 0) flips her book.
Can also be used for the player.

0x102 - Spin Book
~~~~~~~~~~~~~~~~~
::

   0x102 x, y

The specified cheer reader starts spinning her book. ::

   0x102<1>

All cheer readers except the player start spinning their books.

0x103 - Open Book
~~~~~~~~~~~~~~~~~
::

   0x103 x, y

The specified cheer reader stops spinning her book and opens it. ::

   0x103<1>

All cheer readers except the player stop spinning their books and open them.

0x104 - Book Flipping Sound
~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x104
   0x104<1>
   0x104<2>

The above all seem to produce the same book flipping sound effect. Different special values are used in different cues,
but there seems not to be a difference.

0x106 - Opened Book Image
~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x106 type

Sets which image is to be created by the opened books at the end of a spin cue. Values for ``type``, along with
their appearances in the standard cellanim, are:

- 0: Rhythm Tweezers onion.

- 1: DJ Yellow and his student.

- 2: Lockstep.

0x107 - Misc. Animations
~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x107 type

Does one of several animations for all cheer readers. Values for ``type`` are:

- 0: Beat animation.

- 1: Unsure, possibly an animation "reset".

0x109 - Mouth Movement
~~~~~~~~~~~~~~~~~~~~~~
::

   0x109<type> ???

Handles mouth movement for the cheer readers. The argument is unknown, maybe how wide the mouth opens or how long
it stays open? Values for ``type`` are:

- 0: Only the player speaks.

- 1: Only the other cheer readers speak.

- 2: Everybody speaks.

List of subs
~~~~~~~~~~~~
The following are all synchronous.

0x56
   Clears the subtitles of the top row after 3.5 beats.

0x57
   Clears the subtitles of the bottom row after 3.5 beats.

0x58
   "One! Two! Three!" subtitles on the top row.

0x59
   "One! Two! Three!" subtitles on the bottom row.

0x5A
   "It's up to you!" subtitles on the top row.

0x5B
   "It's up to you!" subtitles on the bottom row.

0x5C
   "Rah-rah sis boom bah-BOOM!" subtitles on the top row.

0x5D
   "Rah-rah sis boom bah-BOOM!" subtitles on the bottom row.

0x5E
   "Let's go read a buncha books!" subtitles on the top row.

0x5F
   "Let's go read a buncha books!" subtitles on the bottom row.

0x60
   "OK, it's on!" subtitles on the top row.

0x61
   "OK, it's on!" subtitles on the bottom row.

0x62
   "One! Two! Three!" cue.

0x63
   "It's up to you!" cue.

0x64
   "It's up to you!" cue, with a "Yay!". (unused)

0x65
   "It's up to you!" cue, with an unused effect. (unused)

0x66
   "It's up to you!" cue, with a "Yay!" by all cheer readers. (unused)

0x67
   "Rah-rah sis boom bah-BOOM!" cue, with a sound effect.

0x68-0x6B
   "Let's go read a bunch of books!" cues, with effects like in 0x63-0x66.

0x6C
   "OK, it's on!" cue.

0x6D
   "OK, it's on!" cue, but the top left three cheer readers don't open their books. (used in conjunction with "Let's go...")

0x6E
   Mouth movement for "One! Two! Three!"; only player.

0x6F
   Mouth movement for "One! Two! Three!"; only others.

0x70
   Mouth movement for "One! Two! Three!"; everyone.

0x71-0x73
   Mouth movements for "It's up to you!"; variants as in 0x6E-0x70.

0x74-0x76
   Mouth movements for "Rah-rah sis boom bah-BOOM!"; variants as above.

0x77-0x79
   Mouth movements for "Let's go read a buncha books!"; variants as above.

0x7A-0x7C
   Mouth movements for "OK, it's on!"; variants as above.

0x7D-0x7F
   Single mouth movement; variants as above. (unused)

0x80
   Misc. mouth movements for everyone but the player.

Double Date (0x21)
------------------

0x100 - Input/Ball
~~~~~~~~~~~~~~~~~~
::

   0x100<type> jump, catch, time

Spawns a ball to kick depending on ``type`` such that the first bounce is a beat later. Does not include sound effects.
If ``jump`` is 1, the weasels will jump out of their hole.
``catch`` determines who or what will jump up to catch the ball in the background. The final argument advances
the ball and input by ``time`` ticks, but it mantains the speed it would originally have. It normally stays
at 0 and it's only used for transitions in remixes. Values for ``type`` are:

- 0: Soccer ball. (can be omitted)

- 1: Basketball.

- 2: Football.

Values for ``catch`` are:

- 0: Nothing.

- 1: Football player.

- 2: Two football players.

- 3: Martial artist.

- 4: Basketball/volleyball player?

- 5: Not sure what this is.

- 6: Dog.

0x102-0x104 - Beat Animations
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x102

Beat animation for the player. ::

   0x103

Beat animation for the girl. ::

   0x104

Beat animation for the weasels.

List of subs
~~~~~~~~~~~~
All the following are asynchronous. Note that these assume ``getrest 2`` and ``getrest 3`` are set to appropriate values
that sum to half a beat. They are ``0xE`` and ``0xA`` respectively in the standard game.

0x56
   Soccer ball cue.

0x57
   Soccer ball cue, but the weasels jump up.

0x58
   Basketball cue.

0x59
   Basketball cue, but the weasels jump up. (unused)

0x5A
   Football cue.

0x5B-0x61
   Football cue, but the weasels jump up. ``catch`` values of 0-6 respectively. (unused)

0x62-0x67
   Identical to 0x5C-0x61.

Catch of the Day (0x22)
-----------------------

0x100 - Input
~~~~~~~~~~~~~
::

    0x100 type, time

Sets an A press input for after ``time`` ticks. Values for ``type`` are:

- 0: Quicknibble.

- 1: Pausegill.

- 2: Threefish.

0x101 - Fish Animation
~~~~~~~~~~~~~~~~~~~~~~
::

    0x101 type

Makes a fish appear. Values for ``type`` are:

- 0: Quicknibble.

- 1: Pausegill.

- 2: Threefish.

- 3: Quicknibble, replaced by Threefish.

::

    0x101<1> type

Does an animation for the fish. Values for ``type`` are:

- 0: Touch hook (Quicknibble and Pausegill).

- 1: Tongue up (Threefish).

- 2: Tongue down (Threefish).

::

    0x101<2>

The fish bites onto the hook before a cue.

0x102 - Scene Transition
~~~~~~~~~~~~~~~~~~~~~~~~
::

    0x102 type

A scene transition. Values for ``type`` are:

- -1: Regular scene.

- 0: Eel in foreground.

- 1: Eel in background.

- 2: School of fish in foreground.

List of subs
~~~~~~~~~~~~

All the following are asynchronous. Note that these assume ``getrest 2`` and ``getrest 3`` are set to appropriate values
that sum to half a beat. They will determine how long before the cue fish will bite onto the hook. They are ``0xD`` and
``0xB`` respectively in the standard game.

0x56
    Quicknibble cue.

0x57
    Pausegill cue.

0x58
    Threefish cue.

0x59
    Pausegill "And, go!" voice clip. To be called one beat after 0x57.

0x5A
    Threefish "One, two, three-go!" voice clip. To be called one beat after 0x58.

Micro-Row (0x23)
----------------

Note that not all operations are detailed as they are not well understood.

0x100 - Input
~~~~~~~~~~~~~
::

   0x100 type, time

Sets an A press input for after ``time`` ticks. Values for ``type`` are:

- 0: Regular swim cue.

- 1: First press of a three-step.

- 2: Second press of a three-step.

- 3: Third press of a three-step.

0x101 - Trail Pattern
~~~~~~~~~~~~~~~~~~~~~
::

   0x101 ptr, ???, ???

Sets the trail pattern (?) to the data at ``ptr``. There is currently no way to create custom data for this, and the format
is unknown. The Micro-Row 1 pattern is ``0x101 0x530C98, 0x20, 0``, Micro-Row 2 is ``0x101 0x530B50, 0x20, 0``.

0x102 - Microbe Animations
~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x102 ptr, ???

Sets the pattern in which the microbes move to the data at ``ptr``. Again, there is no way to create custom data. The
Micro-Row 1 pattern is at ``0x102 0x530C50, 9``, Micro-Row 2 is ``0x102 0x530D98, 9``. ::

   0x102<1>

All microbes step once. ::

   0x102<2>

Beat animation for all microbes. ::

   0x102<6>

All microbes do the double-step flash. ::

   0x102<7>

All microbes prepare for a triple-step.

List of subs
~~~~~~~~~~~~

All the following are asynchronous.

0x57
   Regular one-step cue, with "GO!" voice clip. Input is 1.5 beats after start.

0x58
   Regular one-step cue.

0x59
   double-step cue. First input is 1.5 beats after start.

0x5A
   Triple-step cue. First input is 1.5 beats after start.

0x5B
   Cowbell count-in.

0x5C
   Another cowbell count-in.

Fork Lifter (0x24)
------------------

0x100 - Flick Food
~~~~~~~~~~~~~~~~~~
::

    0x100 type

Flicks a food item, to be stabbed after 2 beats. Values for ``type`` are:

- 0: Pea.

- 1: Burger top.

- 2: Burger middle.

- 3: Burger bottom.

0x101 - Ready Finger
~~~~~~~~~~~~~~~~~~~~
::

    0x101 type

Readies the finger with a specified food. Values are identical to those in ``0x100``.

0x102 - Eat Food
~~~~~~~~~~~~~~~~
::

    0x102

Food currently on the fork is eaten. If any of the food is a burger part, the special burger-eating sound effect will be
used.

0x103 - New Food
~~~~~~~~~~~~~~~~
::

    0x103 type

Determines what food will be under the finger after the next flick. Values are identical to those in ``0x100``, with
the addition of 4 meaning nothing.

Hole in One (0x25)
------------------

Note that the code snippet ::

   0x102<2> 1
   0x104<1> 1
   0x101<1> 2

Appears before all instances of Hole in One, and fixes various issues.

0x100 - Spawn Ball
~~~~~~~~~~~~~~~~~~
::

   0x100 type

Spawns a ball to be hit after 1 beat. Values for ``type`` are:

- 0: Monkey throws the ball.

- 1: Mandrill throws the ball. (the ball will only be visible after 1 beat)

0x102 - Player Animation
~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x102

The player prepares to swing. Usually placed one beat before an input. ::

   0x102<1>

Beat animation for the player.

0x103 - Monkey Animation
~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x103

Monkey prepares to throw. ::

   0x103<1>

Monkey throws the ball. ::

   0x103<4>

Beat animation for Monkey.

0x104 - Mandrill Animation
~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x104 type

Mandrill does a part of his throwing animation. Values for ``type`` are:

- 0: Grabs the ball.

- 1: "Hoo" part of the animation; Mandrill holds the ball close.

- 2: Final part of the animation; Mandrill holds the ball behind him and throws.

::

   0x104<2>

Beat animation for Mandrill.

List of subs
~~~~~~~~~~~~

All the following are asynchronous.

0x56
   Monkey cue.

0x57
   Mandrill cue. Mandrill starts properly one beat after the start.

Flipper-Flop (0x26)
-------------------

0x100 - Input
~~~~~~~~~~~~~
::

   0x100 time, type

Sets an input for after ``time`` ticks. Values for ``type`` are:

- 0: Press A

- 1: Press B (flipper roll)

0x101 - Beat Animation
~~~~~~~~~~~~~~~~~~~~~~
::

   0x101

Beat animation for all seals.

0x102 - Raise Flipper
~~~~~~~~~~~~~~~~~~~~~
::

   0x102 type, ???

All seals raise a flipper. If ``type`` is 1, they raise their left flipper, if 0 their right. Second argument always 0.

0x103 - Flip
~~~~~~~~~~~~
::

   0x103

All seals (except the player's) flip (A press).

0x104 - Roll
~~~~~~~~~~~~
::

   0x104 ???

All seals (except the player's) do a flipper roll.

0x105 - Captain Animations
~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x105

The captain steps forward. ::

   0x105<1>

Beat animation for the captain. ::

   0x105<2> type

The captain moves his mouth. Values for ``type`` are:

- 0: Mouth movements for "Attention, company!"

- 1: Mouth movements for "That's it!"

::

   0x105<3> str1, str2

The captain does a beat animation according to the animation name at ``str1`` and a face animation according to the
animation name at ``str2``, or none if 0.

List of subs
~~~~~~~~~~~~

All the following are asynchronous. All flipper roll subs have the first input after 4 beats.

0x59
   Count-in, including cowbells and "Attention, company!"

0x63
   Flip cue (A press). The input is after 4 beats.

0x64
   Triple-flip cue. Audio after 2 beats, input after 4 beats.

0x65
   Flip cue, input after 2 beats.

0x66
   Triple-flip cue, input after 2 beats, audio immediately.

0x67
   Practice flipper roll.

0x68
   Practice 2 flipper rolls.

0x69
   One flipper roll.

0x6A
   One flipper roll without praise at the end.

0x6B
   Two flipper rolls.

0x6C
   Two flipper rolls without praise at the end.

0x6D
   Two flipper rolls without "One, two" voice clips.

0x6E
   Three flipper rolls.

0x6F
   Three flipper rolls without praise at the end.

0x70
   Four flipper rolls.

0x71
   Four flipper rolls without praise at the end and without the "Four" voice clip.

0x72-0x77
   5-10 flipper rolls respectively.

Ringside (0x27)
---------------

0x100 - Input
~~~~~~~~~~~~~
::

   0x100 type, time

Sets an input for after ``time`` ticks. Values for ``type`` are:

- 0: Press A ("yes").

- 1: Press A (flex, first part).

- 2: Press A (flex, second part).

- 3: Press B ("Pose for the fans").

- 4: Press B ("Pose for the fans" with newspaper).

0x101 - View
~~~~~~~~~~~~
::

   0x101 type

Changes the view. Values for ``type`` are:

- 0: View of wrestler and reporter, changes view instantly.

- 1: Zoomed-out view with camera people, zooms smoothly.

0x102 - Camera People Animations
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x102

The camera people in the foreground bounce for a "pose for the fans" cue.

0x103 - Newspaper
~~~~~~~~~~~~~~~~~
::

   0x103 time

A newspaper swirls in over ``time`` ticks. ::

   0x103<1>

Hides the newspaper.

0x104 - Reporter Animations
~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x104 type, ???

Mouth movements. The second argument is unknown, and is 6 for regular mouth movement in cues. Values for ``type`` are:

- 0: "Wubbadubbadub is that true?"

- 1: "Whoa, you go big guy!"

::

   0x104<1>

The reporter raises her microphone. ::

   0x104<2>

The reporter lowers her microphone.

0x106 - Beat Animation
~~~~~~~~~~~~~~~~~~~~~~
::

   0x106

The wrestler does a beat animation.

0x107 - Pose Ready Animation
~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x107

The wrestler readies himself for a pose.

0x108 - Post-Pose Effect
~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x108

Does an effect depending on player performance after a pose (fuzzy noise, radial background). ::

   0x108<1>

Disables the effect.

0x10A - Rhythm Arena Animations
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x10A<1>

Beat animation. ::

   0x10A<2> size1, size2, time

The Rhythm Arena overlay grows or shrinks in size from ``size1`` to ``size2`` (0x100 is regular size, sizes are proportional)
over ``time`` ticks. ::

   0x10A<3> alpha1, alpha2, time

The Rhythm Arena overlay's opacity changes from ``alpha1`` to ``alpha2`` over ``time`` ticks. (max opacity is 0xFF) ::

   0x10A<4>

The lights dim.

List of subs
~~~~~~~~~~~~
All the following are asynchronous.

0x56
   A standard "Wubbadubbadub is that true?" cue. Input is three beats after start.

0x57
   A standard "Whoa, you go big guy!" cue. First input is 3.5 beats after start.

0x58
   A "Whoa, you go big guy!" cue, however, there is no voice clip and the first input is 2.5 beats after start.

0x59
   A standard "Pose for the fans!" cue. Input is three beats after start.

0x5A
   A "Pose for the fans!" cue as above, however, the view doesn't cut back at the end.

0x5B
   A "Pose for the fans!" cue as above, however, the camera people bounce instead of a zoom occurring.

0x5C
   A "Pose for the fans!" cue, however, there is no voice clip and the first input is 2 beats after start.

0x5D-0x5F
   "Pose for the fans!" cues, variants as in 0x59-0x5B, with newspapers.

0x65
   Beat animations for the wrestler 4 beats in a row.

0x66
   Beat animations for the wrestler 172 beats in a row.

Karate Man (0x28)
-----------------

0x100 - Spawn Object
~~~~~~~~~~~~~~~~~~~~
::

   0x100 type

Spawns an object that reaches the player in one beat and produces an input if necessary. Values for ``type`` are:

- 0: A regular pot.

- 1: A pot with a different sound effect (used for offbeat pots).

- 2: Yellow lightbulb.

- 3: Blue lightbulb.

- 4: Alien. (unused)

- 5: Rock.

- 6: Soccer ball.

- 7: Cooking pot.

- 8: A rock, but Karate Joe briefly disappears (unimplemented animation?)

- 9: First object in a combo.

- 0xA: Second object in a combo.

- 0xB: Third object in a combo.

- 0xC: Fourth object in a combo.

- 0xD: Fifth object in a combo.

- 0xE: Final object in a combo.

- 0xF: Kick barrel.

0x102 - Performance-Dependant Animation
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x102

Starts recording player performance for an upcoming animation cue. ::

   0x102<1> type

Does a happy animation according to ``type`` if the player hit everything since the last ``0x102``, or a sad animation
otherwise. Values for ``type`` are:

- 0: Smirk (used for kicks in Karate Man Kicks!)

- 1: Blush and smile (used for combos in Karate Man Combos!)

0x104 - Karate Joe Animations
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x104 type

Does an animation for Karate Joe. Values for ``type`` are:

- 0: Beat animation

- 1: "Ready" animation

- 2: Return to normal stance from ready stance.

0x105 - Text
~~~~~~~~~~~~
::

   0x105<type>

Text appears in the background. Values for ``type`` are:

- 0: "Combo!"

- 1: "Grr!"

- 2: "2!" (unused)

- 3: "3!"

- 4: "4!" (unused)

- 5: "!!"

- 0x63 (99): Clear text.

0x109 - Freeze Animation
~~~~~~~~~~~~
::

   0x109

Karate Joe's combo animation freezes at the end. (Used in Karate Man Combos!)

0x10B - Particle Effects
~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x10B amount

Seems to set the amount of particles coming onto the screen in some amount of time (a beat?) to ``amount``. ::

   0x10B<1> speed, ???

Sets the horizontal speed of the particles to ``speed``. Positive speed indicates rightward motion, negative indicates
leftward. For reference, the fastest speed attained in Karate Man Combos! is ``-0x1400``. ::

   0x10B<3>

Sets the type of particles used to the yellow glowing particles that rise from the bottom of the screen, used in Karate
Man Combos! and Senior, as opposed to the snow-like particles from Karate Man Kicks!.

0x10D - Allow/Restrict Combo
~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x10D flag

If ``flag`` is 1, the player can't combo, and if 0, the player can combo. Note that, by default, the player cannot combo.

0x111 - Background Effect
~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x111

Does a background effect, like the flashing radial patterns in Karate Man and Karate Man Returns!. ::

   0x111<1>

Disables background effect.

List of subs
~~~~~~~~~~~~

All the following are asynchronous.

0x56
   Objects appear for a combo. First input one beat after start.

0x57
   5 beats after the start of the sub, does a happy or sad animation depending on player performance (``0x102<1> 0``)

0x58
   Identical to 0x57, but the delay is 7 beats instead of 5.

0x59
   Identical to 0x57, but uses ``0x102<1> 1`` instead.

0x5A
   Nothing.

0x5B
   A beat animation after 2 beats.

0x5C
   A ready animation after 2 beats.

0x5D
   A "stop ready" animation after 2 beats.

0x5E
   Two beat animations, a half beat apart, after 2 beats.

0x5F
   A beat animation after 2.5 beats.

0x60
   A regular pot cue, with beat animation on the beat of input. Input 2 beats after start.

0x61
   A rock cue, with beat animation on beat of input. Input 2 beats after start.

0x62
   A soccer ball cue, with beat animation on beat of input. Input 2 beats after start.

0x63
   A cooking pot cue, with beat animation on beat of input. Input 2 beats after start.

0x64
   An off-beat pot cue. Input 2.5 beats after start.

0x65
   Two pot cues a half-beat apart. First input 2 beats after start.

0x66
   Off-beat rock cue, with beat animation on beat of input. Input 2.5 beats after start.

0x67
   A zoom out, followed by a yellow lightbulb cue, with beat animation on beat of input. Calls 0x57 one beat after start.
   Input 2 beats after start. Zooms back in 6 beats after start.

0x68
   Identical to 0x67, but does not zoom back in.

0x69
   Identical to 0x68, but does not zoom out, and calls 0x58 instead of 0x57.

0x6A
   Identical to 0x67, but calls 0x59 instead.

0x6B
   Identical to 0x6A, but calls 0x59 a beat later.

0x6C
   Identical to 0x6A, but does not zoom back in.

0x6D-0x71
   Identical to 0x67-0x6A and 0x6C respectively, but spawn a blue lightbulb instead.

0x72
   Spawns the unused alien, with beat animation on beat of input. Input two beats after start. (unused)

0x73
   Calls 0x56, and adds two beat animations one and two beats after the start.

0x74
   A pot cue, but with an unknown addition that causes odd effects (Karate Joe keeps holding his arm out after punching). (unused)

0x75
   Spawns a kick barrel. Input two beats after start.

0x76
   Identical to 0x75, but with a zoom-in included after 4 beats.

0x77
   The text "Combo!" appears for 3 beats.

0x78
   The text "Grr!" appears for 3 beats.

0x79
   The text "Grr!" appears for 1 beat.

0x7A
   The text "2!" appears for 4 beats.

0x7B
   The text "3!" appears for 4 beats.

0x7C
   The text "3!" appears for 2 beats.

0x7D
   The text "4!" appears for 4 beats.

0x7E
   The text "!!" appears for 1 beat.

0x7F
   "One two!" voice clip.

0x80
   Nothing.

0x81
   "Hit 3!" voice clip. Sounds after 0.5 beats.

0x82
   "Hit 4!" voice clip. Note that this is identical to "Hit 3!" in the English version, but distinct in the Japanese version.

0x84
   "Punch-kick!" voice clip.

0x85
   Nothing.

0x86
   Identical to 0x7F.

0x87
   0x7A is called after a half beat.

0x88
   The "3!" text appears accompanied by the "Hit 3!" voice clip. Sounds after 1 beat.

0x89
   Identical to 0x88, but the text appears for a shorter amount of time, and appears later.

0x8A
   Identical to 0x88, but the text appears later.

0x8B
   The "4!" text appears accompanied by the "Hit 4!" voice clip. Sounds after 1 beat.

0x8C
   The text "Combo!" appears after a half beat.

0x8D
   The text "!!" appears after a half beat.

0x8E
   The text "Grr!" appears after a half beat.

0x8F
   Calls 0x84 after a half beat.

Working Dough (0x29)
--------------------

0x100 - Input/Spawn Ball
~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x100 type, flash, time1, time2, time3

Spawns a ball to be hit on the left in ``time1`` ticks, and on the right (by the player) in ``time2`` ticks.
If ``flash`` is 1, hitting it causes the background to flash. The final argument advances the ball and input
by ``time3`` ticks, but it mantains the speed it would originally have. It normally stays at 0 and it's only
used for transitions in remixes. Values for ``type`` are:

- 0: Small ball

- 1: Large ball

- 2: Special large ball

0x101 - Open/Close Gates
~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x101 type1, type2, 1, ???

Opens a gate specified by ``type1`` and ``type2``. These combined seem to specify which to open. Values are:

- (0, 0): Leftmost gate.

- (1, 0): Middle-right gate.

- (1, 1): Middle-left gate.

- (2, 1): Rightmost gate.

::

   0x101 type1, type2, 0

Closes a specified gate.

0x102 - Rocketship control
~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x102<2> pos?, time

Move the rocketship to ``pos`` over ``time`` ticks. Units for ``pos`` are unknown, but the location it is moved to in-game is 0. ::

   0x102<4>

Remove structure around rocketship.

0x103 - Background Guy Animations
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x103<1>

Background guy climbs up the ladder. ::

   0x103<2>

Background guy handles the lever. ::

   0x103<3>

Background guy walks away.

List of subs
~~~~~~~~~~~~
Both are asynchronous.

0x56
   Close gates on the right side after 1 beat.

0x57
   Close gates on the left side after 1 beat.

Figure Fighter (0x2A)
---------------------

0x100 - Input
~~~~~~~~~~~~~
::

    0x100 type, time

Sets up an input for the player after ``time`` ticks. Values for ``type`` are:

- 0: Jab

- 1: First punch of a slow one-two.

- 2: Second punch of a slow one-two.

- 3: First punch of a fast one-two.

- 4: Second punch of a fast one-two.

- 5: First or second punch of a "go-go-go!" cue.

- 6: Final punch of a "go-go-go!" cue.

0x101 - Muscle Doll Animations
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

    0x101 ???

Does a beat animation for the Muscle Doll. The argument's purpose is unknown, but it is always 0, 1, or 2. ::

    0x101<1>

The Muscle Doll huddles up.

0x102 - Background
~~~~~~~~~~~~~~~~~~
::

    0x102

The background becomes black with a spotlight. ::

    0x102<1>

The background returns to normal.

0x103 - Silhouettes
~~~~~~~~~~~~~~~~~~~
::

    0x103 flag

If ``flag`` is 1, turns all foreground objects black. If 0, turns them back to normal.

0x104 - Fade to White
~~~~~~~~~~~~~~~~~~~~~
::

    0x104

The screen fades to white. ::

    0x104<1>

The screen fades from white.

0x106 - Text
~~~~~~~~~~~~
::

    0x106 str

Overlays white text on the screen corresponding to the text with the name pointed to by ``str``. ::

    0x106<1>

Hides the text.

0x107 - Black Bars
~~~~~~~~~~~~~~~~~~
::

    0x107 type

Adds black bars until the next input. Values for ``type`` are:

- 0: Wide.

- 1: Narrow.

- 2: Very narrow.

0x109 - Knock down
~~~~~~~~~~~~~~~~~~
::

    0x109<2>

Makes the next "big punch" (end of combo) knock down the punching bag.

0x10B - Crowd Animations
~~~~~~~~~~~~~~~~~~~~~~~~
::

    0x10B<1> type

Does a crowd beat animation. Values for ``type`` are:

- 0: Default animation.

- 1: Some arms raised.

- 2: Lots of arms raised.

0x10D - Lights
~~~~~~~~~~~~~~
::

    0x10D type

Turns on the specified spotlights. Values for ``type`` are:

- 0: Overhead lights.

- 1-6: Pairs of lights from outermost to innermost.

::

    0x10D<1> type

Turns off the specified spotlights. ::

    0x10D<2> type

Flashes the specified spotlights.

List of subs
~~~~~~~~~~~~
All the following are asynchronous.

0x56
    Calls ``0x109<2>`` after one beat.

0x57
    Calls ``0x109<2>`` after two beats.

0x58
    Calls ``0x109<2>`` after four beats.

0x59
    Narrowing black bars for slow "one two". First black bars appear after 1 beat.

0x5A
    Identical(?) to 0x59.

0x5B
    Narrowing black bars for "go go go!". First black bars appear after 1 beat.

0x5C
    A full "Jab" cue, including voice clip. Input is after 2 beats.

0x5D
    A slow "one two" cue, including voice clip, but not black bars. First input is after 3 beats.

0x5E
    A full fast "one two" cue, including voice clip. First input is after 2 beats.

0x5F
    A "go go go!" cue, including voice clip, but not black bars. First input is after 3 beats.

0x60
    Combines 0x5D and 0x59 to create a full "one two" cue.

0x61
    Identical to 0x60, but uses 0x5A instead of 0x59.

0x62
    Combines 0x5F and 0x5B to create a full "go go go!" cue.

0x63
    An "and" voice clip plays after one beat. Used before a "jab" cue.

0x64
    A different "and" voice clip plays after one beat. Used before a "one-two" cue.

0x65
    Beat animation after 1 beat. (uses ``0x101 0``).

0x66
    Beat animation after 1 beat. (uses ``0x101 1``).

0x67
    Beat animation after 1 beat. (uses ``0x101 2``).

0x68
    Huddle up animation after three quarters of a beat.

Love Rap (0x2B)
---------------

0x100 - Input
~~~~~~~~~~~~~
::

   0x100 type, time, variant

Sets an input depending on ``type`` for after ``time`` ticks. Each has two variants, which are differentiated by ``variant`` (0 or 1).
Values for ``type`` are:

- 0: "Into you"; variants are identical.

- 1: "Crazy into you"

- 2: "Fo' sho'"

- 3: "All about you"

0x101 - Speech/Rap Animations
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x101 1, type, ???

The left rapper raps a line, with speech bubble and animation included. Values for ``type`` are identical to above. ::

   0x101 2, type, ???, variant

MC Adore raps a line, with speech bubble and animation included. ``type`` and ``variant`` are identical to those in ``0x100``. ::

   0x101<1> which

Closes a speech bubble. If ``which`` is 1, closes the left rapper's speech bubble, if 2, closes MC Adore's speech bubble.

0x102 - Beat animations
~~~~~~~~~~~~~~~~~~~~~~~
::

   0x102<type>

Does a beat animation depending on ``type``. Values for ``type`` are:

- 0: Player

- 1: Left rapper

- 2: MC Adore

- 3: The car

0x103 - Poses
~~~~~~~~~~~~~
::

   0x103 type

Changes all rappers' pose. Values for ``type`` are identical to those in ``0x100``.

List of subs
~~~~~~~~~~~~
All the following are synchronous, unless otherwise noted.

0x6E
   4 car beat animations in a row. (async)

0x6F
   8 car beat animations in a row. (async; unused)

0x70
   "Into you" cue (variant 0). Input 1 beat after start.

0x71
   "Into you" cue (variant 0), leading into a "Crazy into you" (pose change).

0x72
   "Into you" cue (variant 0), leading into a "Fo' sho'" (pose change).

0x73
   "Into you" cue (variant 0), leading into an "All about you" (pose change).

0x74-0x77
   Identical to 0x70-0x73; uses variant 1 instead, but this is identical to variant 0.

0x78
   "Crazy into you" cue (variant 0). Input 1.5 beats after start.

0x79
   "Crazy into you" cue (variant 0), leading into an "Into you" (pose change).

0x7A
   "Crazy into you" cue (variant 0), leading into a "Fo' sho'" (pose change).

0x7B
   "Crazy into you" cue (variant 0), leading into an "All about you" (pose change).

0x7C-0x7F
   Identical to 0x78-0x7B; uses variant 1 instead.

0x80
   "Fo' sho'" cue (MC Adore does variant 0, rappers do variant 1). Input 5/6 beat after start.

0x81
   "Fo' sho'" cue as above, leading into an "Into you".

0x82
   "Fo' sho'" cue as above, leading into a "Crazy into you".

0x83
   "Fo' sho'" cue as above, leading into an "All about you".

0x84-0x87
   Like 0x80-0x83; MC Adore does variant 1, rappers do variant 0. Input 2/3 beat after start.

0x88
   "All about you" cue (MC Adore does variant 0, rappers do variant 1). Input 4/3 beat after start.

0x89
   "All about you" cue as above, leading into an "Into you".

0x8A
   "All about you" cue as above, leading into a "Crazy into you".

0x8B
   "All about you" cue as above, leading into a "Fo' sho'".

0x8C-0x8F
   Like 0x88-0x8B; MC Adore does variant 1, rappers do variant 0. Input 7/6 beat after start.

0x90-0xAF
   Identical to 0x70-0x8F, but all delays and rests are doubled (twice as slow). (unused)

Bossa Nova (0x2C)
-----------------

0x100 - Spawn Object
~~~~~~~~~~~~~~~~~~~~
::

    0x100 time, type

Spawns an object to be bounced away ``time`` ticks later. Values for ``type`` are:

- 0: Ball (right)

- 1: Cube (left)

0x101 - Cloud Control
~~~~~~~~~~~~~~~~~~~~~
::

    0x101

The cloud dips down. ::

    0x101<1>

The cloud turns, Bossa and Nova switching places.

0x103 - Voice Variants
~~~~~~~~~~~~~~~~~~~~~~
::

    0x103<1> variant

Sets the variant of the female voice to ``variant``. There are two variants, 1 and 2. ::

    0x103<2> variant

Sets the variant of the male voice to ``variant``.

List of subs
~~~~~~~~~~~~
All the following are asynchronous.

0x5E
    The regular Bossa Nova cue pattern, with male voice clips. (used for right side)
    Also changes the category for results screen accordingly.

0x5F
    The regular Bossa Nova cue pattern with male voice clips. The cloud turns at the end.
    Also changes the category for results screen accordingly.

0x60
    The regular Bossa Nova cue pattern, with female voice clips. (used for left side)
    Also changes the category for results screen accordingly.

0x61
    The regular Bossa Nova cue pattern with female voice clips. The cloud turns at the end.
    Also changes the category for results screen accordingly.

0x62-0x65
    Identical to 0x5E-0x61, but they don't change the category for the results screen. Probably use these in remixes.

Screwbot Factory (0x2D)
-----------------------

0x100 - Input
~~~~~~~~~~~~~
::

    0x100 time, type, ???

Sets an input starting after ``time`` ticks. Third argument is unknown, but seems to always be 1. Values for ``type`` are:

- 0: White robot (hold A one beat)

- 1: Grey robot (hold A two beats)

0x101 - Raise Claw
~~~~~~~~~~~~~~~~~~
::

    0x101

Raises the player's claw-arm-thingy.

0x102 - Crane Animations
~~~~~~~~~~~~~~~~~~~~~~~~
::

    0x102 type

The crane to the right grabs a robot (first part of the animation). Values for ``type`` are like in ``0x100``. ::

    0x102<1> type

The crane to the right brings a robot up above the conveyor belt. ::

    0x102<2> type

The crane drops a robot.

0x103 - Conveyor Control
~~~~~~~~~~~~~~~~~~~~~~~~
::

    0x103

Activates the conveyor belt.

0x105 - Robot Animation
~~~~~~~~~~~~~~~~~~~~~~~
::

    0x105<1> type

Does an animation for all completed robots. Values for ``type`` are:

- 0: All robots return to their normal stance.

- 1: All robots start doing beat animations rapidly.

These are normally used one after another, 0 a quarter beat after 1.

0x106 - Spotlight (unused)
~~~~~~~~~~~~~~~~~~~~~~~~~~
::

    0x106 type

Enables the spotlights. Values for ``type`` are:

- 0: Wide spotlights on both the crane and the arm.

- 1: Narrower version of 0.

- 2: Narrower version of 1; a bit of the background peeks out at the top, which is likely unintended.

- 3: Narrower version of 2.

- 4: Same as 0, but the spotlight on the crane is much wider and the one on the arm is much narrower.

- 5: Opposite of 4.

- 6: Four wide spotlights positioned in a "zig-zag" formation.

- 7: 6, but flipped.

- 8: Spotlight on the robot underneath the arm.

- 9: Narrower version of 8; a bit of the background peeks out at the top.

- 0xA: Narrower version of 9.

::

    0x106<1>

Disables the spotlights.

List of subs
~~~~~~~~~~~~
All the following are asynchronous.

0x56
    White robot cue. Input at 4 beats after start.

0x57
    White robot cue. Input 3 beats after start. (crane drops robot sooner)

0x58
    Grey robot cue. Input 4 beats after start.

0x59
    Grey robot cue. Input 3 beats after start. (crane drops robot sooner)

0x5A
    Beat animations for all robots for 8 beats.

Launch Party (0x2E)
-------------------

0x100 - Countdown
~~~~~~~~~~~~~~~~~
::

   0x100 type

Starts a countdown and sets up an input at the end. Values for ``type`` are:

- 0: White Countdown (1... 0). Input 2 beats after start.

- 1: Red Countdown (3, 2, 1, 0). Input 3 beats after start.

- 2: Blue Countdown (5, 4-3-2-1-0). Input 2 beats after start.

- 3: Green Countdown (7, 6543210). Input 2 beats after start.

0x101 - Counter Control (?; unused)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x101 num, color

Displays the number ``num`` on the countdown. Values for ``color`` are:

- 0: White

- 1: Red

- 2: Blue

- 3: Green

This is not used, as ``0x100`` incorporates these.

0x102 - Rocket Type
~~~~~~~~~~~~~~~~~~~
::

   0x102 type

Changes the type of rocket that pops out of the box. Values for ``type`` correspond to above values. ::

   0x102<1> type

Changes the type after the next rocket has launched.

0x103 - Rocket Appears
~~~~~~~~~~~~~~~~~~~~~~
::

   0x103

A rocket appears from the box.

0x105 - Background
~~~~~~~~~~~~~~~~~~
::

   0x105

The white background fades away. ::

   0x105<1>

The white background is removed instantly. ::

   0x105<2>

The box begins moving up in the background (in reality, the background is moving down).

0x106 - Hover Motion
~~~~~~~~~~~~~~~~~~~~
::

   0x106 1

The box begins circling around, hovering in space.

0x107 - Stars
~~~~~~~~~~~~~
::

   0x107 density

Sets the density of stars flying by. Unit of ``density`` is unknown. ::

   0x107<1> speed

Sets the speed of stars flying by as a percentage of the initial speed seen in the rhythm game. (0x64 or 100 is this initial speed). ::

   0x107<3> size

Sets the size of stars flying by. Units are unknown. Regular size is 0. ::

   0x107<4> z

Sets how far stars can jump "forward" into the foreground from their background positions. Initial value in the
rhythm game is ``0x1E``, and it is ``0x190`` after the first flurry of stars occurs. ::

   0x107<6> 0

Seems to stop stars from appearing at all.

0x109 - Spawn Flying Rocket
~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x109<type> x, y, time

Spawns a flying rocket that will descend from above to ``x`` units (pixels?) to the right and ``y`` below the center.
It will fly off again after ``time`` ticks. Values for ``type`` correspond to ones used in other operations.
There seems to be a limit of four flying rockets. Trying to add any more will not do anything.

0x10A - Rocket Pitch
~~~~~~~~~~~~~~~~~~~~
::

   0x10A 0

Reset pitch? ::

   0x10A<1>

Increment pitch. Wraps back around after four incrementations (?). Normally called every beat.

0x10B - Box Engine
~~~~~~~~~~~~~~~~~~
::

   0x10B

Turns off the box's engine. ::

   0x10B<1>

Turns on the box's engine; regular thrust. ::

   0x10B<2>

Turns on the box's engine; high thrust.

0x10E - Star Rings
~~~~~~~~~~~~~~~~~~
::

   0x10E flag

If ``flag`` is 1, enables light blue rings around stars, if 0, disables. Used in star flurries.

List of subs
~~~~~~~~~~~~
All the following are asynchronous.

0x56
   White countdown (using ``0x101``). Unused, as ``0x100`` incorporates this.

0x57
   Red countdown (using ``0x101``). Unused, as ``0x100`` incorporates this.

0x58
   Blue countdown (using ``0x101``). Unused, as ``0x100`` incorporates this.

0x59
   Green countdown (using ``0x101``). Unused, as ``0x100`` incorporates this.

0x5A
   Pitch reset, followed by a pitch increment every beat forever. Usually called at the start of any instance of Launch
   Party.

Board Meeting (0x2F)
--------------------

0x100 - Input
~~~~~~~~~~~~~
::

   0x100 time, type

Sets up an input for after ``time`` ticks. Values for ``type`` are:

- 0: Regular stop cue.

- 1: Stop after "one-two-three" cue.

0x101 - Individual Pig Animations
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x101 pos

The ``pos`` th pig from the left, starting at 0, does a beat animation. ::

   0x101<1> pos

The specified pig readies itself to start spinning. ::

   0x101<2> pos

The specified pig starts spinning. ::

   0x101<3> pos

The specified pig stops spinning.

0x102 - Bulk Pig Animations
~~~~~~~~~~~~~~~~~~~~~~~~~~~
``0x102`` is identical to ``0x101``, except it does not have an argument and all pigs do the specified animation.

0x103 - Enable/Disable Beat Animations
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x103 flag

If ``flag`` is 0, disable beat animations for all pigs, if 1, enable them.

0x105 - Secretary Animations
~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x105

Beat animation. ::

   0x105<1> num

The secretary moves her mouth ``num`` times. ::

   0x105<5>

"One-two-three" cue animation.

List of subs
~~~~~~~~~~~~
All the following are asynchronous.

0x56
   "One-two-three" voice clip and mouth movements, variant 1.

0x57
   "One-two-three" voice clip and mouth movements, variant 2 (used at the end).

0x58
   The pigs start spinning one after another, from left to right, two beats apart.

0x59
   The pigs start spinning one after another, from left to right, one beat apart.

0x5A
   The pigs start spinning one after another, from left to right, one half beat apart.

0x5B
   The pigs start spinning one after another, from left to right, a third beat apart.

0x5C
   The pigs start spinning one after another, from left to right, a quarter beat apart.

0x5D
   The pigs start spinning simultaneously.

0x5E
   The pigs stop spinning one after another, from left to right, two beats apart. Includes input.

0x5F
   The pigs stop spinning one after another, from left to right, one beat apart. Includes input.

0x60
   The pigs stop spinning one after another, from left to right, one half beat apart. Includes input.

0x61
   The pigs stop spinning one after another, from left to right, a third beat apart. Includes input.

0x62
   The pigs stop spinning one after another, from left to right, a quarter beat apart. Includes input.

0x63
   "One-two-three" cue (variant 1), including input.

0x64
   "One-two-three" cue (variant 2), including input.

Samurai Slice (0x30)
--------------------

0x100 - Spawn Demon
~~~~~~~~~~~~~~~~~~~
::

   0x100 type

Sets an input depending on ``type`` and spawns a demon. Values for ``type`` are:

- 0: Regular demon

- 1: Regular demon (different position; used for two demons in succession)

- 2-8: Individual demons in the many-demon cue.

0x101 - Beat Animation
~~~~~~~~~~~~~~~~~~~~~~
::

   0x101

Beat animation for the samurai.

0x102 - Ready Animation
~~~~~~~~~~~~~~~~~~~~~~~
::

   0x102

The samurai readies himself to strike.

0x104 - Story Overlay
~~~~~~~~~~~~~~~~~~~~~
::

   0x104 0

Reset story. ::

   0x104<1>

Advance to the next page of the story.

0x105 - Falling Particles
~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x105

Snow starts falling down. ::

   0x105<1>

Leaves start falling down. ::

   0x105<2>

Particles stop falling down. ::

   0x105<3> speed

Sets the horizontal speed of the falling particles. Negative speeds indicate leftward movement, while positive speeds
indicate rightward movement.

0x106 - Rain
~~~~~~~~~~~~
::

   0x106 density

Sets the density of rainfall. Only powers of 2 are used for density in the rhythm game, and 8 is the highest density used.

0x107 - Thunder
~~~~~~~~~~~~~~~
::

   0x107<flag>

If ``flag`` is 1, enables lightning striking each time an enemy is hit, if 0, disables.

0x108 - Portal
~~~~~~~~~~~~~~
::

   0x108 time

The demon portal opens over ``time`` ticks. ::

   0x108<1> time

The demon portal closes over ``time`` ticks.

0x109 - Demon Size
~~~~~~~~~~~~~~~~~~
::

   0x109<size>

Sets the size of new demons that appear. ``size`` starts at 0 and increases. The highest value used in the game is 3.

0x10A - Text
~~~~~~~~~~~~
::

   0x10A

Displays the intro text. Seems to be required to undarken the background, sadly. ::

   0x10A<1>

Displays the "To be continued..." (this is a lie) text.

0x10B - Samurai Enter/Leave
~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x10B 0

The samurai leaves (enters the house?) ::

   0x10B<1> 0

The samurai walks on screen.

0x10C - Pinwheel
~~~~~~~~~~~~~~~~
::

   0x10C<flag>

If ``flag`` is 1, demons have pinwheels. If 0, they don't.

0x10D - Practice Demon
~~~~~~~~~~~~~~~~~~~~~~
::

   0x10D

A demon flies onto the screen from the top. ::

   0x10D<1>

The demon flies into the house.

List of subs
~~~~~~~~~~~~
All the following are asynchronous.

0x56
   ``0x100 0``.

0x57
   ``0x100 1``.

0x58
   A whirlwind combo cue. First demon appears immediately.

See-Saw (0x31)
--------------

0x100 - See-Saw Animations & Cues
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x100 1

Start executing See-Saw cues, reading the positions from a location in memory as a sequence of bytes. More on this in ``0x104`` documentation. ::

   0x100<2> index, 0

One of the inspectors does a beat animation. Values for ``index`` are:

- 0: Right

- 1: Left

::

   0x100<3> index, pos

Sets the position of the specified inspector to ``pos``. Values for ``pos`` are:

- 0: On the outside of the see-saw.

- 1: On the inside of the see-saw.

- 2: On the ground besides the see-saw.

0x102 - Enable/Disable High Jumps
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x102 flag

If ``flag`` is 1, enable high jumps (the inspectors will jump much higher than usual when on the outside). If 0, disable.

0x103 - Explode
~~~~~~~~~~~~~~~
::

   0x103

The inspectors explode.

0x104 - Set Position Sequences
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x104 index, ptr

Sets the sequence of positions the specified inspector will have when doing cues to the one at ``ptr``. This is a sequence
of bytes, each representing a single jump. 0 means outside, 1 means inside, 2 means on the ground beside the see-saw, and
5 ends the sequence.

Packing Pests (0x32)
--------------------

0x100 - Spawn Object/Input
~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x100 type

Spawns an object, with corresponding input and sound effects. Input is 1 beat later. Values for ``type`` are:

- 0: Candy

- 1: Spider

- 2: Paycheck

0x101 - Background Objects
~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x101 idx, type

Spawns an object for the ``idx`` th background worker (from the front?) starting at 0. ``type`` is identical to the one in 0x100. ::

   0x101<1> type

Spawns an object for all background workers. ::

   0x101<2> time, type

Spawns an object of type ``type`` for all background workers, in random order and with random delays, over the next ``time`` ticks. ::

   0x101<3> time

Spawns a spider or candy randomly for all background workers, in random order and with random delays, over the next ``time`` ticks.

0x102 - View Change
~~~~~~~~~~~~~~~~~~~
::

   0x102<2> time

The view shifts to the initial view over ``time`` ticks. ::

   0x102<4> time

The view shifts to view your worker's head over ``time`` ticks.

0x103 - Clap
~~~~~~~~~~~~
::

   0x103

Your worker claps for a while. (used in practice)

0x104 - Background Workers
~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x104

Spawns the background workers. ::

   0x104<2> time

The background workers shift into view over ``time`` ticks.

0x105 - Worker Animations
~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x105

Small beat/bob animation (used when a candy is spawned). ::

   0x105<1>

Double up! animation.

0x106 - Curtain
~~~~~~~~~~~~~~~
::

   0x106

Spawns the curtain. ::

   0x106<1>

Removes the curtain by sliding to the left.

0x107 -  Text
~~~~~~~~~~~~~
::

   0x107 flag, type

If ``flag`` is 1, text appears above your worker according to type. If 0, text is removed. Values for ``type`` are:

- 0: Double up!

- 1: !!

List of subs
~~~~~~~~~~~~
All the following are asynchronous.

0x56
   "Double up!" appears for 2 beats.

0x57
   "!!" appears for 1.5 beats.

0x58
   A candy is spawned, with a beat animation. Input after 1 beat.

0x59
   A spider is spawned, with a beat animation. Input after 1 beat.

0x5A
   A paycheck is spawned, with a beat animation. Input after 1 beat. (unused)

0x5B
   A regular pattern (candy then spider, 2 beats apart). First input after 1 beat.

0x5C
   A different pattern (candy then spider, 1.5 beats apart). First input after 1 beat. (unused)

0x5D
   The 0x5C pattern. First input after 1.5 beats. (unused)

0x5E
   A paycheck is spawned, with a beat animation. Input after 1 beat.

0x5F
   A full "Double up!" cue (candy, then spider after 1.5 beats, then another a beat later). First input after 1 beat.

0x60
   "!!" appears after 3.5 beats, along with a sound effect. (unused)

0x61
   A candy is spawned for all background workers. (unused)

0x62
   A spider is spawned for all background workers.

0x63
   A paycheck is spawned for all background workers.

0x64
   A regular pattern is spawned for all background workers (candy then spider after 2 beats).

0x65
   A "Double up!" pattern is spawned for all background workers.

0x66
   The different pattern is spawned for all background workers (candy after a half beat, then spider after 1.5 beats).

Monkey Watch (0x33)
-------------------

0x100 - Input
~~~~~~~~~~~~~
::

   0x100 time

Cues up an input for after ``time`` ticks.

0x101 - Camera Movement
~~~~~~~~~~~~~~~~~~~~~~~
::

   0x101<3> angle, time

The camera moves ``angle`` degrees around the clock in ``time`` ticks.

0x102 - View Change
~~~~~~~~~~~~~~~~~~~
::

   0x102 type, flag

Changes the view. If ``flag`` is not 0, the change is instantaneous. Values for ``type`` are:

- 0: Default view (following your monkey).

- 1: Zoomed-out view of wrist.

0x103 - Watch Monkey Doors
~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x103 pos, flag

The ``pos`` th monkey's (from the top of the watch, starting from 0) door opens and the monkey comes out. If ``flag`` is 1,
this is instantaneous.

0x104 - Watch Monkeys
~~~~~~~~~~~~~~~~~~~~~
::

   0x104<1>

The next monkey after your position on the clock stretches its arm out. ::

   0x104<2>

The next monkey after your position on the clock stretches its arm back in preparation for a high-five. ::

   0x104<3> n

The ``n`` th monkey after your position, starting at 0, becomes purple.

0x106 - Watch Beat Animation
~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x106

Beat animation for the monkey head in the center of the watch.

0x107 - Balloon Control
~~~~~~~~~~~~~~~~~~~~~~~
The exact workings of this operation are as of yet unknown.

List of subs
~~~~~~~~~~~~
All the following are asynchronous.

0x57
   The 3rd and 4th monkeys from your position are purple (Starting at 0).

0x58
   The 4th and 5th monkeys from your position are purple.

0x59
   Sound effects for purple monkeys ("Oo-kii ooki-ki")

0x5C
   Monkeys stretch back their arms every 2 beats forever.

Blue Bear (0x34)
----------------

0x100 - Input/Spawn Food
~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x100 type, long, ???

Spawns a food to be eaten. If ``long`` is nonzero, a longer animation will be used for eating the food. The third
argument is unknown, and is always zero. Values for ``type`` are:

- 0: Donut (right side, 2 beats)

- 1: Cake (left side, 3 beats)

0x101 - Bear Animations
~~~~~~~~~~~~~~~~~~~~~~~
::

   0x101

The bear pushes on the left bag, ejecting a cake. (animation ``body_push_L`` in the cellanim) ::

   0x101<1>

The bear pushes on the right bag, ejecting a donut. (animation ``body_push_R`` in the cellanim) ::

   0x101<2>

The bear pushes on both bags. (animation ``body_push`` in the cellanim) ::

   0x101<3>

The bear opens its mouth to catch food. (animation ``face_ready`` in the cellanim)

0x102 - Memory Animations
~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x102 slot, name

Performs the animation with the name specified by ``name`` (as an ASCII string). It will fade in according to (hardcoded?) rules.
A slot is also specified to allow two to run at the same time. Slot 0 is used for the right side, and 1 for the left.
Animations used in the rhythm game with this operation are:

- ``girl_in_R00``, ``girl_in_R01``

- ``girl_in_L00``, ``girl_in_L01``

- ``lostlove_inL``, ``lostlove_inR`` ::

   0x102<1> slot

The animation in ``slot`` fades out, according to the corresponding ``out`` animation (hardcoded?).

0x103 - Misc. Bear Animations
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x103 body, face

Does the animation specified by ``body`` as an ASCII string for the bear's body (or all of the bear), and the animation
specified by ``face`` (again as an ASCII string) for the bear's face. ::

   0x103<1>

Returns the body to the animation ``body_wait`` and the face to the animation ``face_wait`` (crying if necessary). ::

   0x103<2> body, face

Same as ``0x103`` but returns to ``wait`` animations after the animations are done.

0x104 - Crying Flag
~~~~~~~~~~~~~~~~~~~
::

   0x104 f

If ``f`` is nonzero, the bear is now crying. Otherwise, it is not.

0x105 - Wind Animation
~~~~~~~~~~~~~~~~~~~~~~
::

   0x105

Does the ``wind`` animation from the cellanim.

0x106 - Dream Background Switch
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x106

Enables the epilogue background showing it was all a dream.

Animal Acrobat (0x35)
---------------------

0x100 - Nothing
~~~~~~~~~~~~~~~
::

    0x100

Do nothing.

0x101 - Intro Stuff
~~~~~~~~~~~~~~~~~~~
::

    0x101

Beat animation. ::

    0x101<1> time

Jump off the initial platform, to grab the first animal in ``time`` ticks.

0x102 - Pattern Loading
~~~~~~~~~~~~~~~~~~~~~~~
::

    0x102 delay

Start executing a loaded byte array pattern, with the first animal being grabbed in ``delay`` ticks. This makes the
animals start swinging. ::

    0x102<1> location, size

Loads a byte array of ``size`` bytes, describing the game's patterns. Every byte is an animal. Byte arrays
can be created in Tickflow using the ``bytes`` command. Values for bytes are:

- 0: Single white monkey

- 1: Elephant

- 2: Multiple white monkeys

- 3: Giraffe

- 4: Final platform/end

0x103 - Spotlight
~~~~~~~~~~~~~~~~~
::

    0x103 f

If ``f`` is nonzero, turns spotlights on, otherwise turns them off.

0x104 - Ending Stuff
~~~~~~~~~~~~~~~~~~~~
::

    0x104

The monkey lands. ::

    0x104<1>

The balloon takes off. (used in Jungle Gymnast only)

0x106 - Waterfall
~~~~~~~~~~~~~~~~~
::

    0x106 ???

A waterfall appears. The argument is presumably some sort of distance or time argument, and is ``0x21C`` in every instance.

0x107 - Vine
~~~~~~~~~~~~
::

    0x107 ???

A vine appears in the foreground. The argument is like in 106, but is ``0x320``.

0x108 - Particle Effects
~~~~~~~~~~~~~~~~~~~~~~~~
::

    0x108

Confetti appears. ::

    0x108<1>

Leaves appear. (at the end of Jungle Gymnast)

Tongue Lashing (0x36)
---------------------

0x100 - Yellow bug control
~~~~~~~~~~~~~~~~~~~~~~~~~~
::

    0x100

Spawns a yellow bug. Other 0x100 operations (except ``0x100<1>``) crash if this is not executed beforehand. ::

    0x100<1> distO, distP1, distP2, sound

Sets the path for a yellow bug. ``distO`` determines the distance away from the tongue orthogonally that it starts at (in pixels?),
``distP1`` is the distance to the chameleon, parallel to the tongue, at the start of its path, and ``distP2`` is the
distance parallel to the tongue at the end of its path. Note that negative parallel distances are to the front of the
chameleon. ``sound`` determines the sound effect produced when eaten by the chameleon: ``0x1001134`` is the regular
gulp sound, while ``0x1001135`` is a shorter gulp sound. ::

    0x100<2> time

Sets the bug moving toward the chameleon, to be eaten after ``time`` ticks. ::

    0x100<3>

Beat animation. Also applies to red bugs. ::

    0x100<5>

Despawns the bug. Also applies to red bugs.

0x101 - Red bug control
~~~~~~~~~~~~~~~~~~~~~~~
::

    0x101

Spawns a red bug. ::

    0x101<1> distO1, distP1, distO2, distP2, distP3, sound

Sets the path for a red bug. ``distO1`` determines the orthogonal distance at the start, ``distP1`` the parallel distance
at the start, ``distO2`` the orthogonal distance when it stops and starts to feint, ``distP2`` the parallel distance at that same
point, and finally, ``distP3`` is the parallel distance when eaten. ``sound`` is like in ``0x100<1>``. ::

    0x101<2> time

Moves the bug to its stopping point over ``time`` ticks. ::

    0x101<4> type

Does a feint. The final feint before it's eaten has ``type`` of 1; maybe slightly more extreme feint? ::

    0x101<5> time

Moves the bug from its stopping point to the tongue, to be eaten after ``time`` ticks. ::

    0x101<6>

Stops the bug?

0x102 - Zoom
~~~~~~~~~~~~
::

   0x102<1> z

Sets the zoom to a factor of ``z/0x100``

0x103 - Chameleon Position
~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x103 x, y, tx, ty

Sets the position and target vector of the chameleon. (``x``, ``y``) is the position of the chameleon, and
(``tx``, ``ty``) is the target position, which determines the direction the chameleon is facing. X values are in units
to the right of the middle of the scene; Y values are in units down from the middle of the scene.

0x104 - Chameleon Animations
~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x104

Beat animation. ::

   0x104<1> start, end, time

The chameleon moves along its target axis. It starts at ``start`` away from its set position along the target axis,
and ends at ``end``. The movement takes ``time`` ticks. ::

   0x104<2> start, end, time, delay

Identical to ``0x104<1>``, but only starts after ``delay`` ticks.

0x105 - Zoom+Position
~~~~~~~~~~~~~~~~~~~~~
::

   0x105 z, x, y, tx, ty, delay

This is a combination of ``0x102<1>`` and ``0x103``. It takes place after ``delay`` ticks.

0x106 - Chameleon Grin
~~~~~~~~~~~~~~~~~~~~~~
::

   0x106

Start recording player performance. ::

   0x106<1>

If the player hasn't missed, grin, else do a sad animation. ::

   0x106<2>

Stop recording player performance.

0x107 - Perch
~~~~~~~~~~~~~
::

   0x107<1> type, delay

Changes what the chameleon is perched on after ``delay`` ticks. Values for ``type`` are:

- 0: Hand

- 1: Foot

0x108 - Global Camera Control for whatever reason
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
::

   0x108 x, y, zoom, delay

Sets the global camera pan to (``x``, ``y``) and the global camera zoom to ``zoom`` after ``delay`` ticks.
Note that this is distinct from the other zoom factor in 103/105.

0x109 - Update Effects
~~~~~~~~~~~~~~~~~~~~~~
::

   0x109

Updates location and size of visual effects to line up with the camera's parameters. If not used, visual effects
will retain the location and size they previously had after a camera change.

0x10A - The two guys
~~~~~~~~~~~~~~~~~~~~
::

   0x10A i, flag

Sets the specified guy visible if the flag is nonzero, and invisible otherwise. Values for ``i`` are:

- 0: Nose guy

- 1: Cool guy ::

   0x10A<1> i, x, y

Sets the position of guy ``i`` to (``x``, ``y``) ::

   0x10A<2> i, x1, x2, time

Moves guy ``i`` horizontally from ``x1`` to ``x2`` over ``time`` ticks. ::

   0x10A<3>
   0x10A<4>

Parts of the animations after the bug is either eaten or missed. These should be placed 3/4 beats apart.

List of subs
~~~~~~~~~~~~
Each is asynchronous.

0x56
   Spawns a yellow bug and does its animations. The "three" sound effect happens two beats after the start, and thus
   the input is 4 beats after the start.

0x57
   Spawns a red bug and does its animations. It starts feinting after 3 beats; the input is after 6 beats.

0x58
   Identical to 0x57, but includes operations for the two guys' animations.