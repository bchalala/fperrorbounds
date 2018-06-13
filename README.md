# Build Instructions
1. First install `ant`
2. `ant install-ivy` to get the ivy dependency manager
3. `ant resolve` to resolve all dependencies
4. `ant` to build

# Running FPErrorBound
To run the basic verification, use the following command `ant run -Dinput=[filename]`

Set the flag `-DgenPrecision=genPrecision` to use the precision generation features which geturns the highest verifiable precision for the given confidence and epsilon in the input program.

Additionally, use the `-Ddebug=debug` flag in order to turn on debug print messages.
