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

    private int backgroundColor = Color.argb(255, 13, 18, 33);
    private int panelColor = Color.argb(214, 26, 35, 56);
    private int displayColor = Color.argb(232, 5, 9, 20);
    private int historyColor = Color.argb(166, 17, 24, 39);
    private int numberButtonColor = Color.argb(218, 49, 61, 86);
    private int operatorButtonColor = Color.argb(230, 42, 112, 255);
    private int functionButtonColor = Color.argb(218, 78, 89, 116);
    private int equalsButtonColor = Color.argb(235, 35, 200, 120);
    private int dangerButtonColor = Color.argb(230, 245, 77, 91);
    private int buttonOutlineColor = Color.argb(155, 255, 255, 255);
    private int buttonOutlineWidthDp = 1;
    private int textColor = Color.WHITE;
    private int mutedTextColor = Color.argb(230, 202, 211, 226);
    private String customThemeName = "Custom";
    private int customBackgroundColor = backgroundColor;
    private int customPanelColor = panelColor;
    private int customDisplayColor = displayColor;
    private int customHistoryColor = historyColor;
    private int customNumberButtonColor = numberButtonColor;
    private int customOperatorButtonColor = operatorButtonColor;
    private int customFunctionButtonColor = functionButtonColor;
    private int customEqualsButtonColor = equalsButtonColor;
    private int customDangerButtonColor = dangerButtonColor;
    private int customTextColor = textColor;
    private int customMutedTextColor = mutedTextColor;
    private int customButtonOutlineColor = buttonOutlineColor;
    private int customButtonOutlineWidthDp = buttonOutlineWidthDp;
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
        titleView.setPadding(dp(4), dp(4), dp(4), dp(4));

        drawerToggle = makeHeaderAction("☰ History");
        drawerToggle.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { SetHistoryDrawerOpen(!drawerOpen); }});

        LinearLayout header = new LinearLayout(container.$context());
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(0, 0, 0, dp(8));
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
        modeView.setPadding(0, dp(8), dp(6), dp(8));

        modeSelector = makeTab("Mode: Basic ▾");
        calculatorPanel.setOnTouchListener(new View.OnTouchListener() { public boolean onTouch(View v, MotionEvent event) { if (drawerOpen && event.getAction() == MotionEvent.ACTION_DOWN) SetHistoryDrawerOpen(false); return false; }});
        modeSelector.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { SetAdvancedMode(!advancedMode); }});

        keypad = new LinearLayout(container.$context());
        keypad.setOrientation(LinearLayout.VERTICAL);
        keypad.setGravity(Gravity.TOP);
        keypad.setPadding(0, dp(6), 0, 0);

        LinearLayout drawerHeader = new LinearLayout(container.$context());
        drawerHeader.setOrientation(LinearLayout.HORIZONTAL);
        drawerHeader.setPadding(0, 0, 0, dp(8));
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
        LinearLayout.LayoutParams displayParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(104));
        displayParams.setMargins(0, 0, 0, dp(6));
        calculatorPanel.addView(displayView, displayParams);
        calculatorPanel.addView(modeView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LinearLayout.LayoutParams modeParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(44));
        modeParams.setMargins(0, 0, 0, dp(6));
        calculatorPanel.addView(modeSelector, modeParams);
        calculatorPanel.addView(keypad, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        root.addView(calculatorPanel, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f));
        root.addView(sideDrawer, new LinearLayout.LayoutParams(dp(0), ViewGroup.LayoutParams.MATCH_PARENT));

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
            row.setPadding(0, 0, 0, 0);
            for (int c = 0; c < rows[r].length; c++) {
                TextView button = makeButton(rows[r][c]);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dp(48), 1f);
                params.setMargins(dp(2), 0, dp(2), 0);
                row.addView(button, params);
            }
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(48));
            rowParams.setMargins(0, 0, 0, dp(4));
            keypad.addView(row, rowParams);
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
        button.setBackground(makeRound(buttonColor(label), dp(14), dp(buttonOutlineWidthDp)));
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

    private void applyStyle() { root.setBackgroundColor(backgroundColor); calculatorPanel.setBackgroundColor(backgroundColor); sideDrawer.setBackground(makeRound(historyColor, dp(18), dp(1))); titleView.setText(title); titleView.setTextColor(textColor); drawerToggle.setTextColor(textColor); drawerToggle.setBackground(makeRound(panelColor, dp(18), dp(1))); displayView.setTextColor(textColor); displayView.setBackground(makeRound(displayColor, dp(cornerRadiusDp), dp(1))); modeView.setTextColor(mutedTextColor); modeSelector.setTextColor(textColor); modeSelector.setBackground(makeRound(panelColor, dp(16), dp(1))); refreshModeSelector(); rebuildThemes(); redrawHistory(); }
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
        addThemeOption(3, customThemeName);
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

    @SimpleFunction(description = "Opens or collapses the right history and theme navigation panel.") public void SetHistoryDrawerOpen(boolean value) { drawerOpen = value; ViewGroup.LayoutParams params = sideDrawer.getLayoutParams(); if (params != null) { params.width = drawerOpen ? dp(drawerWidthDp) : 0; sideDrawer.setLayoutParams(params); } sideDrawer.setVisibility(drawerOpen ? View.VISIBLE : View.GONE); drawerToggle.setText(drawerOpen ? "History ›" : "History ☰"); }
    @SimpleFunction(description = "Sets the open history drawer width in dp. Values are clamped between 160 and 360.") public void SetHistoryDrawerWidth(int widthDp) { drawerWidthDp = Math.max(160, Math.min(360, widthDp)); if (drawerOpen) SetHistoryDrawerOpen(true); }
    @SimpleFunction(description = "Returns the open history drawer width in dp.") public int HistoryDrawerWidth() { return drawerWidthDp; }
    @SimpleFunction(description = "Saves the current history to local database storage.") public void SaveHistoryToDatabase() { saveHistoryToDatabase(); }
    @SimpleFunction(description = "Loads saved history from local database storage.") public void LoadHistoryFromDatabase() { loadHistoryFromDatabase(); }
    @SimpleFunction(description = "Selects one of the built-in themes. Use 0 for Midnight, 1 for Ocean, 2 for Solar, or 3 for the custom theme created with CreateTheme.") public void SetTheme(int theme) { selectedTheme = theme; if (theme == 1) { backgroundColor = Color.argb(255, 2, 29, 53); panelColor = Color.argb(215, 10, 73, 108); displayColor = Color.argb(235, 1, 22, 39); historyColor = Color.argb(180, 8, 47, 73); operatorButtonColor = Color.argb(232, 14, 165, 233); equalsButtonColor = Color.argb(235, 45, 212, 191); functionButtonColor = Color.argb(218, 29, 91, 128); numberButtonColor = Color.argb(218, 20, 61, 92); dangerButtonColor = Color.argb(232, 244, 63, 94); buttonOutlineColor = Color.argb(150, 186, 230, 253); buttonOutlineWidthDp = 1; textColor = Color.WHITE; mutedTextColor = Color.argb(230, 186, 230, 253); } else if (theme == 2) { backgroundColor = Color.argb(255, 255, 251, 235); panelColor = Color.argb(225, 254, 243, 199); displayColor = Color.argb(235, 255, 255, 255); historyColor = Color.argb(210, 254, 240, 180); operatorButtonColor = Color.argb(232, 245, 158, 11); equalsButtonColor = Color.argb(235, 22, 163, 74); functionButtonColor = Color.argb(220, 251, 191, 36); numberButtonColor = Color.argb(225, 253, 230, 138); dangerButtonColor = Color.argb(232, 220, 38, 38); buttonOutlineColor = Color.argb(135, 120, 89, 30); buttonOutlineWidthDp = 1; textColor = Color.rgb(31, 41, 55); mutedTextColor = Color.rgb(92, 72, 48); } else if (theme == 3) { selectedTheme = 3; backgroundColor = customBackgroundColor; panelColor = customPanelColor; displayColor = customDisplayColor; historyColor = customHistoryColor; numberButtonColor = customNumberButtonColor; operatorButtonColor = customOperatorButtonColor; functionButtonColor = customFunctionButtonColor; equalsButtonColor = customEqualsButtonColor; dangerButtonColor = customDangerButtonColor; textColor = customTextColor; mutedTextColor = customMutedTextColor; buttonOutlineColor = customButtonOutlineColor; buttonOutlineWidthDp = customButtonOutlineWidthDp; } else { selectedTheme = 0; backgroundColor = Color.argb(255, 13, 18, 33); panelColor = Color.argb(214, 26, 35, 56); displayColor = Color.argb(232, 5, 9, 20); historyColor = Color.argb(166, 17, 24, 39); numberButtonColor = Color.argb(218, 49, 61, 86); operatorButtonColor = Color.argb(230, 42, 112, 255); functionButtonColor = Color.argb(218, 78, 89, 116); equalsButtonColor = Color.argb(235, 35, 200, 120); dangerButtonColor = Color.argb(230, 245, 77, 91); buttonOutlineColor = Color.argb(155, 255, 255, 255); buttonOutlineWidthDp = 1; textColor = Color.WHITE; mutedTextColor = Color.argb(230, 202, 211, 226); } applyStyle(); rebuildKeypad(); }

    private GradientDrawable makeRound(int color, int radius, int stroke) { GradientDrawable d = new GradientDrawable(); d.setColor(color); d.setCornerRadius(radius); if (stroke > 0) d.setStroke(stroke, buttonOutlineColor); return d; }
    private int dp(int value) { return Math.round(value * container.$context().getResources().getDisplayMetrics().density); }


    @SimpleFunction(description = "Sets section background colors: root background, header/mode panel, display, and right history drawer. Use App Inventor color values, including alpha/RGBA colors.") public void SetSectionColors(int background, int panel, int display, int historyPanel) { backgroundColor = background; panelColor = panel; displayColor = display; historyColor = historyPanel; customBackgroundColor = background; customPanelColor = panel; customDisplayColor = display; customHistoryColor = historyPanel; selectedTheme = 3; applyStyle(); rebuildKeypad(); }
    @SimpleFunction(description = "Sets calculator button colors for number, operator, function, equals, and danger/action buttons. Use App Inventor color values, including alpha/RGBA colors.") public void SetButtonColors(int numbers, int operators, int functions, int equals, int danger) { numberButtonColor = numbers; operatorButtonColor = operators; functionButtonColor = functions; equalsButtonColor = equals; dangerButtonColor = danger; customNumberButtonColor = numbers; customOperatorButtonColor = operators; customFunctionButtonColor = functions; customEqualsButtonColor = equals; customDangerButtonColor = danger; selectedTheme = 3; applyStyle(); rebuildKeypad(); }
    @SimpleFunction(description = "Sets the outline color and outline width in dp for all calculator buttons and panels.") public void SetButtonOutline(int outlineColor, int outlineWidthDp) { buttonOutlineColor = outlineColor; buttonOutlineWidthDp = Math.max(0, outlineWidthDp); customButtonOutlineColor = buttonOutlineColor; customButtonOutlineWidthDp = buttonOutlineWidthDp; selectedTheme = 3; applyStyle(); rebuildKeypad(); }
    @SimpleFunction(description = "Creates and applies a custom RGBA-friendly theme with colors for every section, button group, text, and button outline.") public void CreateTheme(String name, int background, int panel, int display, int historyPanel, int numbers, int operators, int functions, int equals, int danger, int text, int mutedText, int outline, int outlineWidthDp) { customThemeName = name == null || name.trim().length() == 0 ? "Custom" : name; customBackgroundColor = background; customPanelColor = panel; customDisplayColor = display; customHistoryColor = historyPanel; customNumberButtonColor = numbers; customOperatorButtonColor = operators; customFunctionButtonColor = functions; customEqualsButtonColor = equals; customDangerButtonColor = danger; customTextColor = text; customMutedTextColor = mutedText; customButtonOutlineColor = outline; customButtonOutlineWidthDp = Math.max(0, outlineWidthDp); selectedTheme = 3; SetTheme(3); }

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
