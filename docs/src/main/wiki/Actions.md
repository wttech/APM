## Actions
The APM scripts are created upon actions. This section describes ..

### What is action?
In APM, an action is a singular operation represented in the script file as a line. The script is processed line by line ignoring all whitespaces including tabulations. If there is a corresponding implementation the copy will be transformed into a concrete action implementation.

Typically, the structure of action is as following

```
ACTION-NAME parameters-list modifiers-list
```

where lists are separated by whitespace. Depending on the action implementation a parameter can be multi-value type. In such case the following `[]` syntax should be used:

```
ALLOW /content [READ, CREATE, MODIFY]
```

### Space or dash?
Whenever an action is constructed upon two keywords they can be written using either dash or whitespace separator. E.g. `CREATE GROUP acme` and `CREATE-GROUP acme` will be just the same. It's encouraged to use the version with whitespace as it's easier to read and write.

The difference is a historical debt and the syntax has been preserved to keep the backwards compatibility.

### How to add comments?
There are two types of actions that are ignored while processing the script.
* Empty Line
* Line starting with `#` sign

This could be used to enrich the script with extra information. E.g.

```
# =========================================
# Minimum required to use front end of site
# =========================================

CREATE GROUP ${groupBase}
```
