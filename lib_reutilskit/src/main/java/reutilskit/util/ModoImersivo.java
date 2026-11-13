package reutilskit.util;

import android.app.Activity;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public final class ModoImersivo {

    private ModoImersivo() { }

    public static void aplicar(Activity atividade) {
        if (atividade == null) return;
        Window window = atividade.getWindow();

        // 1. Desenhar atrás do Notch (Android 9+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            window.setAttributes(params);
        }

        // 2. O WindowCompat resolve o modo imersivo para TODAS as versões do Android
        // (Funciona melhor que o código nativo em celulares com Android modificado)
        WindowCompat.setDecorFitsSystemWindows(window, false);

        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(window, window.getDecorView());
        
        // Esconde as barras de status e navegação
        controller.hide(WindowInsetsCompat.Type.systemBars());
        
        // Permite que as barras apareçam temporariamente ao deslizar na borda
        controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
    }
}