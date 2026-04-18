package com.alecdorrington.server

import munit.CatsEffectSuite

class Stub extends CatsEffectSuite:

  test("placeholder"):
    assertEquals(1 + 1, 2)
