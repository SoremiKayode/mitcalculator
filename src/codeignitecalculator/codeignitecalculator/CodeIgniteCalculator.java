package codeignitecalculator.codeignitecalculator;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

public class CodeIgniteCalculator extends AndroidViewComponent {

    private final ComponentContainer container;
    private final LinearLayout root;
    private final LinearLayout calculatorPanel;
    private final LinearLayout sideDrawer;
    private final TextView titleView;
    private final TextView displayView;
    private final TextView modeView;
    private final TextView drawerToggle;
    private final TextView modeSelector;
    private final LinearLayout historyList;
    private final LinearLayout keypad;
    private final TextView drawerCollapse;
    private final LinearLayout themeList;
    private final ArrayList<String> history = new ArrayList<String>();

    private int backgroundColor = Color.rgb(15, 23, 42);
    private int panelColor = Color.rgb(30, 41, 59);
    private int displayColor = Color.rgb(2, 6, 23);
    private int numberButtonColor = Color.rgb(51, 65, 85);
    private int operatorButtonColor = Color.rgb(37, 99, 235);
    private int functionButtonColor = Color.rgb(71, 85, 105);
    private int equalsButtonColor = Color.rgb(34, 197, 94);
    private int dangerButtonColor = Color.rgb(239, 68, 68);
    private int textColor = Color.WHITE;
    private int mutedTextColor = Color.rgb(185, 194, 210);
    private int cornerRadiusDp = 18;
    private boolean advancedMode = false;
    private boolean drawerOpen = false;
    private int selectedTheme = 0;
    private boolean radians = false;
    private boolean evaluating = false;
    private long lastPressTime = 0;
    private int drawerWidthDp = 220;
    private String expression = "";
    private String title = "Scientific Calculator";
    private static final String HISTORY_PREF = "CodeIgniteCalculatorHistory";
    private final DecimalFormat resultFormat = new DecimalFormat("0.##########");

    public CodeIgniteCalculator(ComponentContainer container) {
        super(container);
        this.container = container;

        root = new LinearLayout(container.$context());
        root.setOrientation(LinearLayout.HORIZONTAL);
        root.setPadding(dp(10), dp(10), dp(10), dp(10));

        calculatorPanel = new LinearLayout(container.$context());
        calculatorPanel.setOrientation(LinearLayout.VERTICAL);

        sideDrawer = new LinearLayout(container.$context());
        sideDrawer.setOrientation(LinearLayout.VERTICAL);
        sideDrawer.setPadding(dp(10), dp(10), dp(10), dp(10));

        titleView = new TextView(container.$context());
        titleView.setTextSize(20);
        titleView.setTypeface(Typeface.DEFAULT_BOLD);
        titleView.setGravity(Gravity.CENTER_VERTICAL);
        titleView.setPadding(dp(4), 0, dp(4), dp(8));

        drawerToggle = makeHeaderAction("☰ History");
        drawerToggle.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { SetHistoryDrawerOpen(!drawerOpen); }});

        LinearLayout header = new LinearLayout(container.$context());
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.addView(titleView, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        header.addView(drawerToggle, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dp(40)));

        displayView = new TextView(container.$context());
        displayView.setTextSize(32);
        displayView.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        displayView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        displayView.setSingleLine(false);
        displayView.setMinLines(2);
        displayView.setPadding(dp(16), dp(10), dp(16), dp(10));

        modeView = new TextView(container.$context());
        modeView.setTextSize(12);
        modeView.setGravity(Gravity.RIGHT);
        modeView.setPadding(0, dp(6), dp(6), dp(6));

        modeSelector = makeTab("Mode: Basic ▾");
        calculatorPanel.setOnTouchListener(new View.OnTouchListener() { public boolean onTouch(View v, MotionEvent event) { if (drawerOpen && event.getAction() == MotionEvent.ACTION_DOWN) SetHistoryDrawerOpen(false); return false; }});
        modeSelector.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { SetAdvancedMode(!advancedMode); }});

        keypad = new LinearLayout(container.$context());
        keypad.setOrientation(LinearLayout.VERTICAL);

        LinearLayout drawerHeader = new LinearLayout(container.$context());
        drawerHeader.setOrientation(LinearLayout.HORIZONTAL);
        TextView historyTitle = makeSectionTitle("History");
        drawerCollapse = makeHeaderAction("›");
        drawerCollapse.setTextSize(24);
        drawerCollapse.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { SetHistoryDrawerOpen(false); }});
        drawerHeader.addView(historyTitle, new LinearLayout.LayoutParams(0, dp(42), 1f));
        drawerHeader.addView(drawerCollapse, new LinearLayout.LayoutParams(dp(42), dp(42)));

        ScrollView historyScroll = new ScrollView(container.$context());
        historyList = new LinearLayout(container.$context());
        historyList.setOrientation(LinearLayout.VERTICAL);
        historyList.setPadding(dp(4), dp(4), dp(4), dp(4));
        historyScroll.addView(historyList);

        themeList = new LinearLayout(container.$context());
        themeList.setOrientation(LinearLayout.VERTICAL);

        sideDrawer.addView(drawerHeader, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        sideDrawer.addView(historyScroll, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        sideDrawer.addView(makeSectionTitle("Themes"), new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(38)));
        sideDrawer.addView(themeList, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        calculatorPanel.addView(header, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        calculatorPanel.addView(displayView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(104)));
        calculatorPanel.addView(modeView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        calculatorPanel.addView(modeSelector, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(44)));
        calculatorPanel.addView(keypad, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        root.addView(sideDrawer, new LinearLayout.LayoutParams(dp(0), ViewGroup.LayoutParams.MATCH_PARENT));
        root.addView(calculatorPanel, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f));

        loadHistoryFromDatabase();
        applyStyle();
        rebuildThemes();
        rebuildKeypad();
        updateDisplay();
        SetHistoryDrawerOpen(false);
        container.$add(this);
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
            row.setPadding(0, dp(2), 0, dp(2));
            for (int c = 0; c < rows[r].length; c++) {
                TextView button = makeButton(rows[r][c]);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dp(52), 1f);
                params.setMargins(dp(2), 0, dp(2), 0);
                row.addView(button, params);
            }
            keypad.addView(row, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        }
        refreshModeSelector();
    }

    private TextView makeButton(final String label) {
        TextView button = new TextView(container.$context());
        button.setText(label);
        button.setTextSize(label.length() > 3 ? 13 : 18);
        button.setTypeface(Typeface.DEFAULT_BOLD);
        button.setGravity(Gravity.CENTER);
        button.setTextColor(textColor);
        button.setBackground(makeRound(buttonColor(label), dp(14), 0));
        button.setPadding(dp(3), dp(3), dp(3), dp(3));
        button.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { press(label); }});
        return button;
    }

    private void press(String key) {
        long now = System.currentTimeMillis();
        if (now - lastPressTime < 140) return;
        lastPressTime = now;
        if (drawerOpen && !"HIST".equals(key)) SetHistoryDrawerOpen(false);
        String nextExpression = expression;
        if ("C".equals(key)) nextExpression = "";
        else if ("⌫".equals(key)) nextExpression = expression.length() == 0 ? "" : expression.substring(0, expression.length() - 1);
        else if ("=".equals(key)) { calculate(); updateDisplay(); ButtonClicked(key, expression); return; }
        else if ("±".equals(key)) nextExpression = appendIfValid("-");
        else if ("π".equals(key)) nextExpression = appendIfValid("π");
        else if ("÷".equals(key)) nextExpression = appendIfValid("/");
        else if ("×".equals(key)) nextExpression = appendIfValid("*");
        else if ("√".equals(key)) nextExpression = appendIfValid("sqrt(");
        else if ("EXP".equals(key)) nextExpression = appendIfValid("E");
        else if ("RAD".equals(key)) { radians = !radians; }
        else if ("HIST".equals(key)) { SetHistoryDrawerOpen(!drawerOpen); }
        else if ("ANS".equals(key)) nextExpression = appendIfValid(lastAnswer());
        else if (isFunction(key)) nextExpression = appendIfValid(key + "(");
        else nextExpression = appendIfValid(key);
        expression = nextExpression;
        updateDisplay();
        ButtonClicked(key, expression);
    }

    private void calculate() {
        if (evaluating) return;
        String input = expression.trim();
        if (input.length() == 0 || !isCompleteExpression(input)) return;
        evaluating = true;
        try {
            double value = new Parser(input).parse();
            String result = format(value);
            history.add(0, input + " = " + result);
            expression = result;
            saveHistoryToDatabase();
            redrawHistory();
            CalculationCompleted(input, result);
        } catch (Exception e) {
            CalculationError(input, e.getMessage() == null ? "Invalid expression" : e.getMessage());
        } finally {
            evaluating = false;
        }
    }

    private void updateDisplay() { displayView.setText(expression.length() == 0 ? "0" : expression); modeView.setText((radians ? "RAD" : "DEG") + "  •  " + (advancedMode ? "Advanced" : "Basic")); refreshModeSelector(); }
    private void redrawHistory() { historyList.removeAllViews(); if (history.size() == 0) { TextView empty = makeHistoryRow("No calculations yet"); empty.setGravity(Gravity.CENTER); historyList.addView(empty); return; } for (final String h : history) { TextView row = makeHistoryRow(h); row.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { expression = h; updateDisplay(); HistoryItemSelected(h); }}); LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); params.setMargins(0, dp(3), 0, dp(3)); historyList.addView(row, params); } }
    private String lastAnswer() { return history.size() == 0 ? "0" : history.get(0).substring(history.get(0).lastIndexOf("=") + 1).trim(); }
    private String appendIfValid(String token) { String candidate = expression + token; return canAccept(candidate) ? candidate : expression; }
    private boolean canAccept(String candidate) { if (candidate.length() > 96) return false; if (candidate.contains("..") || candidate.contains("EE")) return false; if (candidate.startsWith("*") || candidate.startsWith("/") || candidate.startsWith("%") || candidate.startsWith("^")) return false; if (openParens(candidate) < 0) return false; return hasValidTokenFlow(candidate); }
    private int openParens(String value) { int open = 0; for (int i = 0; i < value.length(); i++) { char ch = value.charAt(i); if (ch == '(') open++; else if (ch == ')') open--; if (open < 0) return -1; } return open; }
    private boolean isCompleteExpression(String value) { if (openParens(value) != 0) return false; char last = value.charAt(value.length() - 1); return "+-*/%^E(.".indexOf(last) < 0; }
    private boolean hasValidTokenFlow(String value) { char previous = 0; boolean decimalInNumber = false; for (int i = 0; i < value.length(); i++) { char ch = value.charAt(i); if (Character.isDigit(ch)) { previous = ch; continue; } if (ch == '.') { if (decimalInNumber) return false; decimalInNumber = true; } else if (!Character.isDigit(ch)) decimalInNumber = false; if (isBinaryOperator(ch) && (previous == 0 || isBinaryOperator(previous) || previous == '(') && ch != '-') return false; if (ch == ')' && (previous == 0 || isBinaryOperator(previous) || previous == '(' || previous == '.')) return false; if (ch == '(' && (Character.isDigit(previous) || previous == ')' || previous == 'π')) return false; previous = ch; } return true; }
    private boolean isBinaryOperator(char ch) { return ch == '+' || ch == '*' || ch == '/' || ch == '%' || ch == '^' || ch == 'E'; }
    private boolean isFunction(String key) { return "sin cos tan asin acos atan sinh cosh tanh log ln".contains(key); }
    private String format(double v) { if (Double.isNaN(v) || Double.isInfinite(v)) throw new RuntimeException("Math error"); return resultFormat.format(v); }
    private int buttonColor(String label) { if ("=".equals(label)) return equalsButtonColor; if ("C".equals(label) || "⌫".equals(label) || "HIST".equals(label)) return dangerButtonColor; if ("+-×÷^()%±".contains(label)) return operatorButtonColor; if (Character.isDigit(label.charAt(0)) || ".".equals(label)) return numberButtonColor; return functionButtonColor; }

    private void applyStyle() { root.setBackgroundColor(backgroundColor); calculatorPanel.setBackgroundColor(backgroundColor); sideDrawer.setBackground(makeRound(panelColor, dp(18), dp(1))); titleView.setText(title); titleView.setTextColor(textColor); drawerToggle.setTextColor(textColor); drawerToggle.setBackground(makeRound(panelColor, dp(18), 0)); displayView.setTextColor(textColor); displayView.setBackground(makeRound(displayColor, dp(cornerRadiusDp), dp(1))); modeView.setTextColor(mutedTextColor); modeSelector.setTextColor(textColor); modeSelector.setBackground(makeRound(panelColor, dp(16), dp(1))); refreshModeSelector(); rebuildThemes(); redrawHistory(); }
    private void refreshModeSelector() { if (modeSelector != null) modeSelector.setText("Mode: " + (advancedMode ? "Advanced" : "Basic") + " ▾"); }
    private TextView makeTab(String label) { TextView tab = new TextView(container.$context()); tab.setText(label); tab.setTextSize(15); tab.setTypeface(Typeface.DEFAULT_BOLD); tab.setGravity(Gravity.CENTER); tab.setPadding(dp(8), 0, dp(8), 0); return tab; }
    private TextView makeHeaderAction(String label) { TextView action = makeTab(label); action.setPadding(dp(12), 0, dp(12), 0); return action; }
    private TextView makeSectionTitle(String label) { TextView title = makeTab(label); title.setGravity(Gravity.CENTER_VERTICAL); title.setTextColor(textColor); return title; }
    private TextView makeHistoryRow(String value) { TextView row = new TextView(container.$context()); row.setText(value); row.setTextColor(mutedTextColor); row.setTextSize(14); row.setGravity(Gravity.RIGHT); row.setPadding(dp(10), dp(8), dp(10), dp(8)); row.setBackground(makeRound(Color.TRANSPARENT, dp(10), dp(1))); return row; }

    private void rebuildThemes() {
        if (themeList == null) return;
        themeList.removeAllViews();
        addThemeOption(0, "Midnight");
        addThemeOption(1, "Ocean");
        addThemeOption(2, "Solar");
    }

    private void addThemeOption(final int theme, String label) {
        TextView option = makeTab((selectedTheme == theme ? "✓ " : "") + label);
        option.setTextColor(textColor);
        option.setBackground(makeRound(selectedTheme == theme ? operatorButtonColor : Color.TRANSPARENT, dp(14), dp(1)));
        option.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { SetTheme(theme); }});
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(42));
        params.setMargins(0, dp(4), 0, dp(4));
        themeList.addView(option, params);
    }

    private void saveHistoryToDatabase() {
        SharedPreferences.Editor editor = container.$context().getSharedPreferences(HISTORY_PREF, 0).edit();
        editor.putString("history", History());
        editor.apply();
    }

    private void loadHistoryFromDatabase() {
        history.clear();
        String stored = container.$context().getSharedPreferences(HISTORY_PREF, 0).getString("history", "");
        if (stored.length() > 0) {
            String[] rows = stored.split("\n");
            for (String row : rows) if (row.trim().length() > 0) history.add(row);
        }
        redrawHistory();
    }

    @SimpleFunction(description = "Opens or collapses the left history and theme navigation panel.") public void SetHistoryDrawerOpen(boolean value) { drawerOpen = value; ViewGroup.LayoutParams params = sideDrawer.getLayoutParams(); if (params != null) { params.width = drawerOpen ? dp(drawerWidthDp) : 0; sideDrawer.setLayoutParams(params); } sideDrawer.setVisibility(drawerOpen ? View.VISIBLE : View.GONE); drawerToggle.setText(drawerOpen ? "‹ History" : "☰ History"); }
    @SimpleFunction(description = "Sets the open history drawer width in dp. Values are clamped between 160 and 360.") public void SetHistoryDrawerWidth(int widthDp) { drawerWidthDp = Math.max(160, Math.min(360, widthDp)); if (drawerOpen) SetHistoryDrawerOpen(true); }
    @SimpleFunction(description = "Returns the open history drawer width in dp.") public int HistoryDrawerWidth() { return drawerWidthDp; }
    @SimpleFunction(description = "Saves the current history to local database storage.") public void SaveHistoryToDatabase() { saveHistoryToDatabase(); }
    @SimpleFunction(description = "Loads saved history from local database storage.") public void LoadHistoryFromDatabase() { loadHistoryFromDatabase(); }
    @SimpleFunction(description = "Selects one of the built-in themes. Use 0 for Midnight, 1 for Ocean, or 2 for Solar.") public void SetTheme(int theme) { selectedTheme = theme; if (theme == 1) { backgroundColor = Color.rgb(3, 37, 65); panelColor = Color.rgb(12, 74, 110); displayColor = Color.rgb(2, 24, 43); operatorButtonColor = Color.rgb(14, 165, 233); equalsButtonColor = Color.rgb(45, 212, 191); functionButtonColor = Color.rgb(30, 94, 131); numberButtonColor = Color.rgb(21, 63, 94); textColor = Color.WHITE; mutedTextColor = Color.rgb(186, 230, 253); } else if (theme == 2) { backgroundColor = Color.rgb(255, 251, 235); panelColor = Color.rgb(254, 243, 199); displayColor = Color.WHITE; operatorButtonColor = Color.rgb(245, 158, 11); equalsButtonColor = Color.rgb(22, 163, 74); functionButtonColor = Color.rgb(251, 191, 36); numberButtonColor = Color.rgb(253, 230, 138); textColor = Color.rgb(31, 41, 55); mutedTextColor = Color.rgb(92, 72, 48); } else { selectedTheme = 0; backgroundColor = Color.rgb(15, 23, 42); panelColor = Color.rgb(30, 41, 59); displayColor = Color.rgb(2, 6, 23); numberButtonColor = Color.rgb(51, 65, 85); operatorButtonColor = Color.rgb(37, 99, 235); functionButtonColor = Color.rgb(75, 85, 105); equalsButtonColor = Color.rgb(34, 197, 94); textColor = Color.WHITE; mutedTextColor = Color.rgb(185, 194, 210); } applyStyle(); rebuildKeypad(); }

    private GradientDrawable makeRound(int color, int radius, int stroke) { GradientDrawable d = new GradientDrawable(); d.setColor(color); d.setCornerRadius(radius); if (stroke > 0) d.setStroke(stroke, Color.rgb(68, 78, 99)); return d; }
    private int dp(int value) { return Math.round(value * container.$context().getResources().getDisplayMetrics().density); }

    @SimpleFunction(description = "Sets the displayed calculator expression.") public void SetExpression(String value) { expression = value == null ? "" : value; updateDisplay(); }
    @SimpleFunction(description = "Returns the current calculator expression or result.") public String Expression() { return expression; }
    @SimpleFunction(description = "Evaluates the current display expression and returns the result.") public String Calculate() { calculate(); updateDisplay(); return expression; }
    @SimpleFunction(description = "Clears the display.") public void Clear() { expression = ""; updateDisplay(); }
    @SimpleFunction(description = "Clears calculation history.") public void ClearHistory() { history.clear(); saveHistoryToDatabase(); redrawHistory(); }
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
    @SimpleEvent(description = "Triggered when a history item is selected.") public void HistoryItemSelected(String historyItem) { EventDispatcher.dispatchEvent(this, "HistoryItemSelected", historyItem); }

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
