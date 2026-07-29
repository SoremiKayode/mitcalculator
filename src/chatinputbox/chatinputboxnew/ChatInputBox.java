package chatinputbox.chatinputboxnew;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.runtime.AndroidViewComponent;
import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.EventDispatcher;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ChatInputBox extends AndroidViewComponent {

    private final ComponentContainer container;
    private final LinearLayout root;
    private final TextView titleView;
    private final TextView displayView;
    private final TextView modeView;
    private final LinearLayout historyList;
    private final LinearLayout keypad;
    private final LinearLayout tabBar;
    private final TextView basicTab;
    private final TextView advancedTab;
    private final ArrayList<String> history = new ArrayList<String>();

    private int backgroundColor = Color.rgb(18, 22, 31);
    private int panelColor = Color.rgb(29, 35, 49);
    private int displayColor = Color.rgb(9, 13, 20);
    private int numberButtonColor = Color.rgb(48, 56, 73);
    private int operatorButtonColor = Color.rgb(255, 149, 0);
    private int functionButtonColor = Color.rgb(75, 85, 105);
    private int equalsButtonColor = Color.rgb(34, 197, 94);
    private int dangerButtonColor = Color.rgb(239, 68, 68);
    private int textColor = Color.WHITE;
    private int mutedTextColor = Color.rgb(185, 194, 210);
    private int cornerRadiusDp = 18;
    private boolean advancedMode = false;
    private boolean radians = false;
    private String expression = "";
    private String title = "Scientific Calculator";
    private final DecimalFormat resultFormat = new DecimalFormat("0.##########");

    public ChatInputBox(ComponentContainer container) {
        super(container);
        this.container = container;

        root = new LinearLayout(container.$context());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(12), dp(12), dp(12), dp(12));

        titleView = new TextView(container.$context());
        titleView.setTextSize(20);
        titleView.setTypeface(Typeface.DEFAULT_BOLD);
        titleView.setGravity(Gravity.CENTER_VERTICAL);
        titleView.setPadding(dp(4), 0, dp(4), dp(8));

        displayView = new TextView(container.$context());
        displayView.setTextSize(32);
        displayView.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        displayView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        displayView.setSingleLine(false);
        displayView.setMinLines(2);
        displayView.setPadding(dp(16), dp(12), dp(16), dp(12));

        modeView = new TextView(container.$context());
        modeView.setTextSize(12);
        modeView.setGravity(Gravity.RIGHT);
        modeView.setPadding(0, dp(6), dp(6), dp(6));

        ScrollView historyScroll = new ScrollView(container.$context());
        historyList = new LinearLayout(container.$context());
        historyList.setOrientation(LinearLayout.VERTICAL);
        historyList.setPadding(dp(8), dp(8), dp(8), dp(8));
        historyScroll.addView(historyList);

        tabBar = new LinearLayout(container.$context());
        tabBar.setOrientation(LinearLayout.HORIZONTAL);
        tabBar.setPadding(0, dp(8), 0, dp(8));
        basicTab = makeTab("Basic");
        advancedTab = makeTab("Advanced");
        basicTab.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { SetAdvancedMode(false); }});
        advancedTab.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { SetAdvancedMode(true); }});
        tabBar.addView(basicTab, new LinearLayout.LayoutParams(0, dp(42), 1f));
        tabBar.addView(advancedTab, new LinearLayout.LayoutParams(0, dp(42), 1f));

        HorizontalScrollView keyScroll = new HorizontalScrollView(container.$context());
        keypad = new LinearLayout(container.$context());
        keypad.setOrientation(LinearLayout.VERTICAL);
        keyScroll.addView(keypad);

        root.addView(titleView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        root.addView(displayView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(104)));
        root.addView(modeView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        root.addView(historyScroll, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        root.addView(tabBar, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        root.addView(keyScroll, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        applyStyle();
        rebuildKeypad();
        updateDisplay();
        Width(ViewGroup.LayoutParams.MATCH_PARENT);
        Height(ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public View getView() { return root; }

    @SimpleFunction(description = "Adds the calculator UI into an Arrangement component.")
    public void AddToArrangement(AndroidViewComponent arrangement) {
        ViewGroup parent = (ViewGroup) arrangement.getView();
        if (root.getParent() != null) ((ViewGroup) root.getParent()).removeView(root);
        parent.addView(root, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void rebuildKeypad() {
        keypad.removeAllViews();
        String[][] rows = advancedMode
                ? new String[][]{{"sin", "cos", "tan", "log", "ln", "√"}, {"asin", "acos", "atan", "π", "e", "^"}, {"sinh", "cosh", "tanh", "!", "%", "RAD"}, {"7", "8", "9", "÷", "(", ")"}, {"4", "5", "6", "×", "C", "⌫"}, {"1", "2", "3", "-", "ANS", "="}, {"0", ".", "±", "+", "EXP", "HIST"}}
                : new String[][]{{"C", "⌫", "(", ")", "÷"}, {"7", "8", "9", "×", "√"}, {"4", "5", "6", "-", "^"}, {"1", "2", "3", "+", "%"}, {"0", ".", "±", "π", "="}};
        for (int r = 0; r < rows.length; r++) {
            LinearLayout row = new LinearLayout(container.$context());
            row.setOrientation(LinearLayout.HORIZONTAL);
            for (int c = 0; c < rows[r].length; c++) row.addView(makeButton(rows[r][c]), new LinearLayout.LayoutParams(dp(64), dp(54)));
            keypad.addView(row);
        }
        refreshTabs();
    }

    private TextView makeButton(final String label) {
        TextView button = new TextView(container.$context());
        button.setText(label);
        button.setTextSize(label.length() > 3 ? 13 : 18);
        button.setTypeface(Typeface.DEFAULT_BOLD);
        button.setGravity(Gravity.CENTER);
        button.setTextColor(Color.WHITE);
        button.setBackground(makeRound(buttonColor(label), dp(14), 0));
        button.setPadding(dp(4), dp(4), dp(4), dp(4));
        button.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { press(label); }});
        return button;
    }

    private void press(String key) {
        if ("C".equals(key)) expression = "";
        else if ("⌫".equals(key)) expression = expression.length() == 0 ? "" : expression.substring(0, expression.length() - 1);
        else if ("=".equals(key)) calculate();
        else if ("±".equals(key)) expression += "-";
        else if ("π".equals(key)) expression += "π";
        else if ("÷".equals(key)) expression += "/";
        else if ("×".equals(key)) expression += "*";
        else if ("√".equals(key)) expression += "sqrt(";
        else if ("EXP".equals(key)) expression += "E";
        else if ("RAD".equals(key)) { radians = !radians; }
        else if ("HIST".equals(key)) { ClearHistory(); }
        else if ("ANS".equals(key)) expression += lastAnswer();
        else if (isFunction(key)) expression += key + "(";
        else expression += key;
        updateDisplay();
        ButtonClicked(key, expression);
    }

    private void calculate() {
        String input = expression.trim();
        if (input.length() == 0) return;
        try {
            double value = new Parser(input).parse();
            String result = format(value);
            history.add(0, input + " = " + result);
            expression = result;
            redrawHistory();
            CalculationCompleted(input, result);
        } catch (Exception e) {
            expression = "Error";
            CalculationError(input, e.getMessage() == null ? "Invalid expression" : e.getMessage());
        }
    }

    private void updateDisplay() { displayView.setText(expression.length() == 0 ? "0" : expression); modeView.setText((radians ? "RAD" : "DEG") + "  •  " + (advancedMode ? "Advanced" : "Basic")); }
    private void redrawHistory() { historyList.removeAllViews(); for (String h : history) { TextView row = new TextView(container.$context()); row.setText(h); row.setTextColor(mutedTextColor); row.setTextSize(14); row.setGravity(Gravity.RIGHT); row.setPadding(dp(8), dp(5), dp(8), dp(5)); historyList.addView(row); } }
    private String lastAnswer() { return history.size() == 0 ? "0" : history.get(0).substring(history.get(0).lastIndexOf("=") + 1).trim(); }
    private boolean isFunction(String key) { return "sin cos tan asin acos atan sinh cosh tanh log ln".contains(key); }
    private String format(double v) { if (Double.isNaN(v) || Double.isInfinite(v)) throw new RuntimeException("Math error"); return resultFormat.format(v); }
    private int buttonColor(String label) { if ("=".equals(label)) return equalsButtonColor; if ("C".equals(label) || "⌫".equals(label) || "HIST".equals(label)) return dangerButtonColor; if ("+-×÷^()%±".contains(label)) return operatorButtonColor; if (Character.isDigit(label.charAt(0)) || ".".equals(label)) return numberButtonColor; return functionButtonColor; }

    private void applyStyle() { root.setBackgroundColor(backgroundColor); titleView.setText(title); titleView.setTextColor(textColor); displayView.setTextColor(textColor); displayView.setBackground(makeRound(displayColor, dp(cornerRadiusDp), dp(1))); modeView.setTextColor(mutedTextColor); historyList.setBackground(makeRound(panelColor, dp(14), 0)); refreshTabs(); }
    private void refreshTabs() { if (basicTab == null) return; basicTab.setTextColor(textColor); advancedTab.setTextColor(textColor); basicTab.setBackground(makeRound(advancedMode ? panelColor : operatorButtonColor, dp(20), 0)); advancedTab.setBackground(makeRound(advancedMode ? operatorButtonColor : panelColor, dp(20), 0)); }
    private TextView makeTab(String label) { TextView tab = new TextView(container.$context()); tab.setText(label); tab.setTextSize(15); tab.setTypeface(Typeface.DEFAULT_BOLD); tab.setGravity(Gravity.CENTER); return tab; }
    private GradientDrawable makeRound(int color, int radius, int stroke) { GradientDrawable d = new GradientDrawable(); d.setColor(color); d.setCornerRadius(radius); if (stroke > 0) d.setStroke(stroke, Color.rgb(68, 78, 99)); return d; }
    private int dp(int value) { return Math.round(value * container.$context().getResources().getDisplayMetrics().density); }

    @SimpleFunction(description = "Sets the displayed calculator expression.") public void SetExpression(String value) { expression = value == null ? "" : value; updateDisplay(); }
    @SimpleFunction(description = "Returns the current calculator expression or result.") public String Expression() { return expression; }
    @SimpleFunction(description = "Evaluates the current display expression and returns the result.") public String Calculate() { calculate(); updateDisplay(); return expression; }
    @SimpleFunction(description = "Clears the display.") public void Clear() { expression = ""; updateDisplay(); }
    @SimpleFunction(description = "Clears calculation history.") public void ClearHistory() { history.clear(); redrawHistory(); }
    @SimpleFunction(description = "Returns calculation history separated by new lines.") public String History() { StringBuilder b = new StringBuilder(); for (int i = 0; i < history.size(); i++) { if (i > 0) b.append("\n"); b.append(history.get(i)); } return b.toString(); }
    @SimpleFunction(description = "Shows either the basic or advanced scientific keypad.") public void SetAdvancedMode(boolean value) { advancedMode = value; rebuildKeypad(); updateDisplay(); }
    @SimpleFunction(description = "Sets trigonometry mode. True uses radians; false uses degrees.") public void SetRadians(boolean value) { radians = value; updateDisplay(); }

    @SimpleProperty(description = "Calculator title text.") public void Title(String value) { title = value == null ? "" : value; applyStyle(); }
    @SimpleProperty(description = "Calculator title text.") public String Title() { return title; }
    @SimpleProperty(description = "Background color.") public void BackgroundColor(int value) { backgroundColor = value; applyStyle(); }
    @SimpleProperty(description = "Display panel color.") public void DisplayColor(int value) { displayColor = value; applyStyle(); }
    @SimpleProperty(description = "Text color.") public void TextColor(int value) { textColor = value; applyStyle(); rebuildKeypad(); }
    @SimpleProperty(description = "Corner radius in dp.") public void CornerRadius(int value) { cornerRadiusDp = value; applyStyle(); rebuildKeypad(); }

    @SimpleEvent(description = "Triggered whenever a calculator key is clicked. Returns key and display text.") public void ButtonClicked(String key, String displayText) { EventDispatcher.dispatchEvent(this, "ButtonClicked", key, displayText); }
    @SimpleEvent(description = "Triggered after a successful calculation. Returns expression and result.") public void CalculationCompleted(String sourceExpression, String result) { EventDispatcher.dispatchEvent(this, "CalculationCompleted", sourceExpression, result); }
    @SimpleEvent(description = "Triggered when calculation fails. Returns expression and error message.") public void CalculationError(String sourceExpression, String message) { EventDispatcher.dispatchEvent(this, "CalculationError", sourceExpression, message); }

    private class Parser {
        private final String s; private int pos = -1, ch; Parser(String s) { this.s = s.replace("π", String.valueOf(Math.PI)); next(); }
        void next() { ch = (++pos < s.length()) ? s.charAt(pos) : -1; }
        boolean eat(int c) { while (ch == ' ') next(); if (ch == c) { next(); return true; } return false; }
        double parse() { double x = expression(); if (pos < s.length()) throw new RuntimeException("Unexpected: " + (char) ch); return x; }
        double expression() { double x = term(); for (;;) { if (eat('+')) x += term(); else if (eat('-')) x -= term(); else return x; } }
        double term() { double x = factor(); for (;;) { if (eat('*')) x *= factor(); else if (eat('/')) x /= factor(); else if (eat('%')) x %= factor(); else return x; } }
        double factor() { if (eat('+')) return factor(); if (eat('-')) return -factor(); double x; int start = pos; if (eat('(')) { x = expression(); if (!eat(')')) throw new RuntimeException("Missing )"); } else if ((ch >= '0' && ch <= '9') || ch == '.') { while ((ch >= '0' && ch <= '9') || ch == '.' || ch == 'E') next(); x = Double.parseDouble(s.substring(start, pos)); } else if (ch >= 'a' && ch <= 'z') { while (ch >= 'a' && ch <= 'z') next(); String f = s.substring(start, pos); if ("e".equals(f)) x = Math.E; else { x = factor(); x = applyFunction(f, x); } } else throw new RuntimeException("Unexpected input"); if (eat('^')) x = Math.pow(x, factor()); if (eat('!')) x = factorial(x); return x; }
        double applyFunction(String f, double x) { if ("sqrt".equals(f)) return Math.sqrt(x); if ("sin".equals(f)) return Math.sin(angle(x)); if ("cos".equals(f)) return Math.cos(angle(x)); if ("tan".equals(f)) return Math.tan(angle(x)); if ("asin".equals(f)) return unangle(Math.asin(x)); if ("acos".equals(f)) return unangle(Math.acos(x)); if ("atan".equals(f)) return unangle(Math.atan(x)); if ("sinh".equals(f)) return Math.sinh(x); if ("cosh".equals(f)) return Math.cosh(x); if ("tanh".equals(f)) return Math.tanh(x); if ("log".equals(f)) return Math.log10(x); if ("ln".equals(f)) return Math.log(x); throw new RuntimeException("Unknown function: " + f); }
        double angle(double x) { return radians ? x : Math.toRadians(x); } double unangle(double x) { return radians ? x : Math.toDegrees(x); }
        double factorial(double x) { if (x < 0 || x != Math.floor(x)) throw new RuntimeException("Factorial needs whole number"); double r = 1; for (int i = 2; i <= (int) x; i++) r *= i; return r; }
    }
}
