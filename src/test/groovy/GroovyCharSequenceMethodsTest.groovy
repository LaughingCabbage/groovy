/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package groovy

/**
 * Tests for DGM methods on CharSequence.
 *
 * @author Paul King
 */
class GroovyCharSequenceMethodsTest extends GroovyTestCase {

    def s1 = 'Today is Thu Jul 28 06:38:07 EST 2011'
    def cs1 = [
            toString:{ -> s1 },
            subSequence:{ int f, int t -> s1.substring(f, t) },
            length:{ -> s1.length() },
            charAt:{ int i -> s1.chars[i] },
    ] as CharSequence
    def s2 = 'Foobar'
    def cs2 = [
            toString:{ -> s2 },
            subSequence:{ int f, int t -> s2.substring(f, t) },
            length:{ -> s2.length() },
            charAt:{ int i -> s2.chars[i] },
    ] as CharSequence
    def cs3 = [
            toString: { -> '''\
                |Foo
                |bar
                |'''
            }
    ] as CharSequence
    def csEmpty = [toString:{->''}, length:{->0}] as CharSequence

    void testIsCase() {
        // direct
        assert cs2.isCase('Foobar')
        assert !cs2.isCase('Baz')
        // typical usage
        switch(cs2) {
            case 'Foobar': break
            default: assert false, 'Should not get here'
        }
    }

    void testSize() {
        assert cs1.size() == 37
    }

    void testGetAtRange() {
        assert cs2.getAt(1..3) == 'oob'
        assert cs2[1..3] == 'oob'
        assert cs2.getAt(3..1) == 'boo'
        assert cs2.getAt(-3..-1) == 'bar'
        assert cs2[-3..-1] == 'bar'
    }

    void testGetAtCollection() {
        assert cs2.getAt([0, 4, 5]) == 'Far'
        assert cs2[1, 3, 5] == 'obr'
        assert cs2[3, 5, 2] == 'bro'
    }

    void testGetAtEmptyRange() {
        assert cs2.getAt(new EmptyRange(null)) == ''
    }

    void testReverse() {
        assert cs2.reverse() == 'rabooF'
    }

    void testStripMargin() {
        assert cs3.stripMargin() == 'Foo\nbar\n'
    }

    void testStripIndent() {
        assert cs3.stripIndent() == '|Foo\n|bar\n|'
    }

    void testIsAllWhitespace() {
        assert !cs2.isAllWhitespace()
        assert ([toString:{->' \t\n\r'}] as CharSequence).isAllWhitespace()
    }

    void testReplace() {
        assert cs2.replaceFirst(~/[ab]/, csEmpty) == 'Fooar'
        assert cs2.replaceAll(~/[ab]/, csEmpty) == 'Foor'
    }

    void testMatches() {
        assert cs2.matches(~/.oo.*/)
        assert !cs2.matches(~/.*z.*/)
    }

    void testFind() {
        def csDigits = [toString:{->/\d{4}/}] as CharSequence
        assert cs1.find(csDigits) == '2011'
        assert cs1.find(csDigits, {"--$it--"}) == '--2011--'
        assert cs1.find(~/\d\d:\d\d/) == '06:38'
        assert cs1.find(~/\d\d:\d\d/, {"|$it|"}) == '|06:38|'
    }

    void testFindAll() {
        def csDigits = [toString:{->/\d\d/}] as CharSequence
        assert cs1.findAll(csDigits) == ['28', '06', '38', '07', '20', '11']
        assert cs1.findAll(csDigits, {"<$it>"}) == ['<28>', '<06>', '<38>', '<07>', '<20>', '<11>']
        assert cs1.findAll(~/\s\d\d/) == [' 28', ' 06', ' 20']
        assert cs1.findAll(~/\s\d\d/, {"<$it >"}) == ['< 28 >', '< 06 >', '< 20 >']
    }

    void testPad() {
        assert cs2.padLeft(10) == '    Foobar'
        assert cs2.padLeft(10, '+') == '++++Foobar'
        assert cs2.padRight(10) == 'Foobar    '
        assert cs2.padRight(10, '+') == 'Foobar++++'
        assert cs2.center(10) == '  Foobar  '
        assert cs2.center(10, '+') == '++Foobar++'
    }

    void testDrop() {
        assert cs2.drop(3) == 'bar'
    }

    void testAsBoolean() {
        assert cs1 && cs2
        assert !csEmpty
    }

    void testLeftShift() {
        assert cs2.leftShift('___').toString() == 'Foobar___'
        assert (cs2 << 'baz').toString() == 'Foobarbaz'
    }

    void testSplit() {
        def parts = cs1.split()
        assert parts instanceof CharSequence[]
        assert parts.size() == 8
        assert parts[2] == 'Thu'
    }

    void testTokenize() {
        def parts = cs1.tokenize()
        assert parts instanceof List
        assert parts.size() == 8
        assert parts[2] == 'Thu'
        parts = cs1.tokenize(':')
        assert parts.size() == 3
        assert parts[1] == '38'
    }

    void testCapitalize() {
        def csfoo = [toString:{->'foo'}] as CharSequence
        assert csfoo.capitalize() == 'Foo'
        assert cs2.capitalize() == 'Foobar'
    }

    void testExpand() {
        def csfoobar = [toString:{->'foo\tbar'}] as CharSequence
        assert csfoobar.expand() == 'foo     bar'
        assert csfoobar.expand(4) == 'foo bar'
        csfoobar = [toString:{->'\tfoo\n\tbar'}] as CharSequence
        assert csfoobar.expand(4) == '    foo\n    bar'
    }

    void testUnexpand() {
        def csfoobar = [toString:{->'foo     bar'}] as CharSequence
        assert csfoobar.unexpand() == 'foo\tbar'
        assert csfoobar.unexpand(4) == 'foo\t\tbar'
        csfoobar = [toString:{->'     foo\n    bar'}] as CharSequence
        assert csfoobar.unexpand(4) == '\t foo\n\tbar'
    }

    void testPlus() {
        assert cs2.plus(42) == 'Foobar42'
        assert cs2 + 42 == 'Foobar42'
    }

    void testMinus() {
        def csoo = [toString:{->'oo'}] as CharSequence
        assert cs2.minus(42) == 'Foobar'
        assert cs2.minus(csoo) == 'Fbar'
        assert cs2 - csoo == 'Fbar'
    }

    void testContains() {
        def csoo = [toString:{->'oo'}] as CharSequence
        def csbaz = [toString:{->'baz'}] as CharSequence
        assert cs2.contains(csoo)
        assert !cs2.contains(csbaz)
    }

    void testCount() {
        def cszero = [toString:{->'0'}] as CharSequence
        def csbar = [toString:{->'|'}] as CharSequence
        assert cs1.count(cszero) == 3
        assert cs3.count(csbar) == 3
    }

    void testNext() {
        assert cs2.next() == 'Foobas'
        assert ++cs2 == 'Foobas'
    }

    void testPrevious() {
        assert cs2.previous() == 'Foobaq'
        assert --cs2 == 'Foobaq'
    }

    void testMultiply() {
        assert cs2.multiply(2) == 'FoobarFoobar'
        assert cs2 * 3 == 'FoobarFoobarFoobar'
    }

    void testToInteger() {
        def csFourteen = [toString:{->'014'}] as CharSequence
        assert csFourteen.isInteger()
        def fourteen = csFourteen.toInteger()
        assert fourteen instanceof Integer
        assert fourteen == 14
    }

    void testToLong() {
        def csFourteen = [toString:{->'014'}] as CharSequence
        assert csFourteen.isLong()
        def fourteen = csFourteen.toLong()
        assert fourteen instanceof Long
        assert fourteen == 14L
    }

    void testToShort() {
        def csFourteen = [toString:{->'014'}] as CharSequence
        def fourteen = csFourteen.toShort()
        assert fourteen instanceof Short
        assert fourteen == 14
    }

    void testToBigInteger() {
        def csFourteen = [toString:{->'014'}] as CharSequence
        assert csFourteen.isBigInteger()
        def fourteen = csFourteen.toBigInteger()
        assert fourteen instanceof BigInteger
        assert fourteen == 14G
    }

    void testToFloat() {
        def csThreePointFive = [toString:{->'3.5'}] as CharSequence
        assert csThreePointFive.isFloat()
        def threePointFive = csThreePointFive.toFloat()
        assert threePointFive instanceof Float
        assert threePointFive == 3.5
    }

    void testToDouble() {
        def csThreePointFive = [toString:{->'3.5'}] as CharSequence
        assert csThreePointFive.isDouble()
        def threePointFive = csThreePointFive.toDouble()
        assert threePointFive instanceof Double
        assert threePointFive == 3.5D
    }

    void testToBigDecimal() {
        def csThreePointFive = [toString:{->'3.5'}] as CharSequence
        assert csThreePointFive.isBigDecimal()
        assert csThreePointFive.isNumber()
        def threePointFive = csThreePointFive.toBigDecimal()
        assert threePointFive instanceof BigDecimal
        assert threePointFive == 3.5G
    }

    void testEachLine() {
        def result = []
        cs3.eachLine{ line, num -> result << "$num:${line.size()}" }
        assert result == ["0:20", "1:20", "2:17"]
        result = []
        cs3.eachLine(10){ line, num -> result << "$num:${line.size()}" }
        assert result == ["10:20", "11:20", "12:17"]
    }

    void testSplitEachLine() {
        def regexOp = /\s*\*\s*/
        def csOp = [toString:{->regexOp}] as CharSequence
        def csTwoLines = [toString:{->'10*15\n11 * 9'}] as CharSequence
        def result = []
        csTwoLines.splitEachLine(csOp){ left, right -> result << left.toInteger() * right.toInteger() }
        assert result == [150, 99]
        result = []
        csTwoLines.splitEachLine(~regexOp){ left, right -> result << left.toInteger() * right.toInteger() }
        assert result == [150, 99]
    }

    void testReadLines() {
        def lines = cs3.readLines()
        assert lines.size() == 3
        assert lines[1].trim() == '|bar'
    }

    void testToList() {
        def chars = cs2.toList()
        assert chars.size() == 6
        assert chars[0] == 'F'
        assert chars[3] == 'b'
        assert chars[-1] == 'r'
    }

    void testToSet() {
        def chars = cs2.toSet()
        assert chars.size() == 5
        assert 'F' in chars
        assert 'b' in chars
    }

    void testGetChars() {
        def chars = cs2.chars
        assert chars instanceof char[]
        assert chars.size() == 6
        assert chars[0] == 'F'
        assert chars[3] == 'b'
        assert chars[-1] == 'r'
    }

    private enum Coin { penny, nickel, dime, quarter }
    void testAsType() {
        def csDime = [toString:{->'dime'}] as CharSequence
        def dime = csDime as Coin
        assert dime instanceof Coin
        assert dime == Coin.dime
    }

    void testEachMatch() {
        def result = []
        def regexDigits = /(\d)(.)(\d)/
        def csDigits = [toString:{->regexDigits}] as CharSequence
        assert cs1.eachMatch(csDigits) { all, first, delim, second -> result << "$first $delim $second" }
        assert result == ['8   0', '6 : 3', '8 : 0', '2 0 1']
        result = []
        assert cs1.eachMatch(~regexDigits) { all, first, delim, second -> result << "$first $delim $second" }
        assert result == ['8   0', '6 : 3', '8 : 0', '2 0 1']
    }

    void testTr() {
        assert cs1.tr(':uoa', '/___') == 'T_d_y is Th_ J_l 28 06/38/07 EST 2011'
    }

    void testReplaceAllFirst() {
        def csDigit = [toString:{->/\d/}] as CharSequence
        def csUnder = [toString:{->/_/}] as CharSequence

        assert cs1.replaceAll(~/\d/, csUnder) == 'Today is Thu Jul __ __:__:__ EST ____'
        assert cs1.replaceAll(csDigit, csUnder) == 'Today is Thu Jul __ __:__:__ EST ____'
        assert cs1.replaceAll(csDigit, { '_' }) == 'Today is Thu Jul __ __:__:__ EST ____'
        assert cs1.replaceFirst(~/\d/, csUnder) == 'Today is Thu Jul _8 06:38:07 EST 2011'
        assert cs1.replaceFirst(csDigit, csUnder) == 'Today is Thu Jul _8 06:38:07 EST 2011'
        assert cs1.replaceFirst(csDigit, { '_' }) == 'Today is Thu Jul _8 06:38:07 EST 2011'
    }

    void testNormalizeDenormalize() {
        def text = 'the quick brown\nfox jumped\r\nover the lazy dog'
        def csText = [toString : { -> text }] as CharSequence
        assert csText.normalize() == text.normalize()
        assert csText.normalize().denormalize() == text.normalize().denormalize()
    }

}
