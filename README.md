GDDL: General-purpose Data Description Language
=============

GDDL is a library for accessing data files described in the GDDL syntax.

The source code is licensed under the 3-clause BSD license.
See [LICENSE.txt](/LICENSE.txt) for details.

The old readme was severely out of date. The current one isn't as exhaustive as I'd like it to be but it will work for now. (Sorry I don't have an eBNF grammar at the moment)

Here's instead a basic description of the syntax:

## Syntax

Starting with GDDL2, GDDL is a superset of Json. Meaning any valid JSON is also valid GDDL. However, it also supports alternative syntax for objects, and has additional features.

### Literals

GDDL supports the following literal types:

- Nulls: `null` and `nil` keywords define a value with no value.
- Integers: supported in decimal `12345` and hex `0x1f3a` formats.
- Floating-point: basic `1.0`, `.23` and scientific notation `1E+23`.
- Strings: can be single-quoted `'a'` or double-quoted `"a"`. Both work the same, and support escapes `"a \n b"`, including unicode hex `'\u2022'`.

### Lists

GDDL defines a list as a sequence of elements delimited by `[` `]`.

```
[ 0, 1, 2, 3, "a", 5.0 ]
```

### Maps

GDDL defines a map as a set of key-value pairs delimited by `{` `}`. Like in json, the keys are always strings, but they can also be defined as unquoted identifiers.

The preferred delimiter for key-value pairs is the equal sign `=`

```
{
    a = 1,
    "b" = [ 3.0, "word" ]
}
```

but using the colon `:` is also supported.

```
{
    a: 1,
    "b": [ 3.0, "word" ]
}
```

### Integrated Query Language

GDDL defines a query language that allows referencing parts of a document. This query language can be used externally, but also within a document to reuse parts of the same document elsewhere.

If a query starts with a slash `/`, it starts searching from the root element of the document, otherwise it's referenced from the container where the query is located.

A list can be queried using range notation:

- `[2]` returns the entry with index 2 (zero-based)
- `[1..]` returns everything starting with the element at index 1
- `[3..4]` returns the range from index 3 to 4 (exclusive)
- `[3...4]` returns the range from index 3 to 4 (inclusive)
- `[^1]` returns the second to last element
- `[1,^1]` returns everything except the tips

A map can be queried by the name of the key. 

- In `{a:3, b:4}` the query `/a` would return `3`.

When querying a value inside a collection, a slash `/` is used to separate between them. However, it is not needed to have a slash before the range brace.

- `/a/b` queries the value of key `b` inside the value of key `a`.
- `/a[15]` queries the value of index 15 inside the value of key `a`.

For backward compatibility with the reference feature of GDDL1, the queries can use `:` instead of `/` as delimiter, but the entire query must use the same delimiter, mixin them is disallowed. 

## The Library

### Parsing a document

GDDL provides convenience methods to parse a file.

```java
var doc = GDDL.fromFile("Test.txt");
var root = doc.getRoot();
```

### Saving a document

A document can be saved directly,

```java
doc.write(Paths.get("output.txt"));
```

but any value can be formatted to a string by using the formatter.

```java
var text = Formatter.formatNice(data);

System.out.println(text);
Files.writeString(Paths.get("output.txt"), text);
```

Both methods of saving support custom formatter options. 

A few stock formats are provided in `FormatterOptions`:
- `COMPACT_HUMAN` (default): Serializes without additional whitespace or line breaks. Uses `=` for maps.
- `NICE_HUMAN`: Serializes with indentation, whitespaces around delimiters, and `=` for maps.
- `COMPACT_JSON`: Always writes quoted strings for map keys. Uses `:` for maps.
- `NICE_JSON`: Serializes with indentation and whitespaces around delimiters. Always writes quoted strings for map keys. Uses `:` for maps.
- `COMPACT_JSON5`: Effectively the same as `COMPACT_HUMAN` but using `:` for maps.
- `NICE_JSON5`: Effectively the same as `NICE_HUMAN` but using `:` for maps.

### Constructing a document 

The document object can be created through `GddlDocument.create()`, with an optional root value.

A literal value can be created through `GddlValue.of(value)`. 

Null values can be created through `GddlValue.nullValue()`.

Lists can be created through `GddlList.empty()` and `GddlList.of(items...)`.

Maps can be created through `GddlMap.empty()` and `GddlMap.of(items...)`.

### Simplifying a document

When parsing, sometimes it is wanted for the document to have all the queries resolved.

The `simplify()` method returns a copy of the document that replaces all query references with their actual value.

### Querying data

The document structure can be accessed explicitly.


Alternatively, the query language can be used to perform a query in code,

```java
var result = doc.getRoot().query("'key name'[1]/b");
```
