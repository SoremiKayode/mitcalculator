# Add any ProGuard configurations specific to this
# extension here.

-keep public class codeignitecalculator.codeignitecalculator.CodeIgniteCalculator {
    public *;
 }
-keeppackagenames gnu.kawa**, gnu.expr**

-optimizationpasses 4
-allowaccessmodification
-mergeinterfacesaggressively

-repackageclasses 'codeignitecalculator/codeignitecalculator/repack'
-flattenpackagehierarchy
-dontpreverify
