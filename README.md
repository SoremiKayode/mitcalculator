## CodeIgniteCalculator Extension

An MIT App Inventor 2 extension created with Rush. It provides a visible, drop-in scientific calculator component with a display label, basic and advanced keypad tabs, expression evaluation, trigonometric functions, and calculation history.

The extension blocks and visible component are named `CodeIgniteCalculator` instead of `ChatInputBox`. Add the component directly to a Screen or VerticalArrangement, or use `AddToArrangement` to place it into an Arrangement at runtime.

### Fixing the `external_comps/chatinputbox.chatinputboxnew/classes.jar` load error

If App Inventor shows an error like:

```text
Unable to load file: .../assets/external_comps/chatinputbox.chatinputboxnew/classes.jar
```

then the project is still referencing the previous `ChatInputBoxNew` extension package. Remove the old extension from the App Inventor project, import a freshly built `CodeIgniteCalculator.aix`, then drag the `CodeIgniteCalculator` component back onto the screen.

This repository no longer tracks Rush or IDE cache files that can preserve the old package name. If you have a local checkout from before this fix, delete `.rush/` before building so Rush regenerates metadata from `rush.yml` and the Java package.
