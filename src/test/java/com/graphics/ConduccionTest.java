package com.graphics; // Permite probar los métodos protegidos del juego desde el mismo paquete.

import junit.framework.TestCase; // Proporciona las comprobaciones de JUnit usadas por Maven.

/** Comprueba las reglas de conducción sin abrir una ventana OpenGL. */
public class ConduccionTest extends TestCase {

    /** Comprueba que el mapa ampliado cumple el tamaño y la cantidad mínima de manzanas y parques. */
    public void testMapaAmpliado() {
        assertTrue(clase1.MAPA.length >= 11); // Exige al menos 11 filas.
        int edificios = 0; // Cuenta las manzanas con edificio.
        int parques = 0; // Cuenta las manzanas de parque.
        for (int[] fila : clase1.MAPA) { // Recorre cada fila del mapa.
            assertEquals(clase1.MAPA.length, fila.length); // Exige un mapa cuadrado.
            for (int celda : fila) { // Recorre cada celda de la fila.
                if (celda == 1) { // Detecta un edificio.
                    edificios++; // Suma un edificio.
                }
                if (celda == 2) { // Detecta un parque.
                    parques++; // Suma un parque.
                }
            }
        }
        assertTrue(edificios >= 12); // Exige al menos 12 manzanas con edificios.
        assertTrue(parques >= 4); // Exige al menos 4 parques.
        assertEquals(clase1.MAPA.length * clase1.CELDA / 2, clase1.LIMITE, 0f); // Exige que el borde dependa del tamaño del mapa.
        assertTrue(clase3.LUCES.length >= 9); // Exige al menos nueve farolas.
    }

    /** Comprueba que las calles del recorrido conectan el inicio y las entregas. */
    public void testCallesYDestinosTransitables() {
        clase2 juego = new clase2(); // Crea el estado del juego sin ejecutar run().
        for (float z = -50; z <= 50; z += 0.25f) { // Recorre la calle del borde este, en el sector nuevo.
            assertTrue(juego.puedeCircular(50, z)); // Exige que cada punto del tramo sea transitable.
        }
        for (float x = -50; x <= 50; x += 0.25f) { // Recorre la avenida del borde norte completa.
            assertTrue(juego.puedeCircular(x, -50)); // Comprueba la conexión entre la ciudad original y el sector este.
        }
        for (float x = -30; x <= 50; x += 0.25f) { // Recorre la avenida sur desde el inicio hasta el borde este.
            assertTrue(juego.puedeCircular(x, 30)); // Comprueba el acceso desde el inicio hacia los sectores nuevos.
        }
        assertTrue(juego.puedeCircular(-10, -10)); // Verifica que un cruce interior permite circular.
        assertFalse(juego.puedeCircular(0, 0)); // Verifica que la manzana del origen bloquea al vehículo.
        assertTrue(juego.puedeCircular(50, -50)); // Primera entrega sobre la calle.
        assertTrue(juego.puedeCircular(50, 50)); // Segunda entrega sobre la calle.
        assertTrue(juego.puedeCircular(-30, 30)); // Tercera entrega y punto inicial sobre la calle.
    }

    /** Comprueba edificios, parques, aceras y bordes del mapa. */
    public void testObstaculosYLimites() {
        clase2 juego = new clase2(); // Crea un vehículo con su radio de colisión inicial.
        assertFalse(juego.puedeCircular(-20, -20)); // Rechaza el centro de un edificio.
        assertFalse(juego.puedeCircular(0, -40)); // Rechaza el centro de un parque.
        assertFalse(juego.puedeCircular(40, 40)); // Rechaza el centro de un edificio del sector sureste nuevo.
        assertFalse(juego.puedeCircular(-25.5f, -20)); // Rechaza una posición cuyo círculo invade la acera.
        assertFalse(juego.puedeCircular(54, 0)); // Rechaza una posición demasiado cercana al borde derecho ampliado.
        assertFalse(juego.puedeCircular(0, -56)); // Rechaza una posición exterior al borde norte ampliado.
        assertTrue(juego.puedeCircular(-27, -20)); // Acepta una posición con separación suficiente de la acera.
    }

    /** Comprueba que la chata sigue al auto manteniendo la longitud de la lanza y se dobla al girar. */
    public void testChataSigueAlAuto() {
        clase2 juego = new clase2(); // Crea el auto con la chata alineada detrás.
        assertTrue(juego.puedeCircular(juego.chataX, juego.chataZ, clase2.RADIO_CHATA)); // La chata empieza sobre la calle.
        float[] chata = null; // Guardará la última pose calculada de la chata.
        for (int paso = 0; paso < 50; paso++) { // Simula una curva hacia la izquierda en pasos pequeños.
            juego.angulo += 0.01f; // Gira un poco el auto en cada paso.
            juego.autoX -= (float) Math.sin(juego.angulo) * 0.2f; // Avanza hacia su frente en X.
            juego.autoZ -= (float) Math.cos(juego.angulo) * 0.2f; // Avanza hacia su frente en Z.
            chata = juego.arrastrarChata(juego.autoX, juego.autoZ, juego.angulo); // Calcula el arrastre.
            juego.chataX = chata[0]; // Acepta la posición X de la chata.
            juego.chataZ = chata[1]; // Acepta la posición Z de la chata.
            juego.chataAngulo = chata[2]; // Acepta la orientación de la chata.
        }
        float engancheX = juego.autoX + (float) Math.sin(juego.angulo) * clase2.ENGANCHE; // Ubica la bola de enganche en X.
        float engancheZ = juego.autoZ + (float) Math.cos(juego.angulo) * clase2.ENGANCHE; // Ubica la bola de enganche en Z.
        float largo = (float) Math.hypot(chata[0] - engancheX, chata[1] - engancheZ); // Mide la lanza resultante.
        assertEquals(clase2.LANZA, largo, 0.001f); // Exige que la lanza no se estire ni se encoja.
        assertTrue(chata[2] > 0 && chata[2] < juego.angulo); // La chata gira en el mismo sentido, pero menos que el auto.
    }

    /** Comprueba que el reinicio restaura la posición y detiene el vehículo. */
    public void testReinicio() {
        clase4 juego = new clase4(); // Crea la versión final sin inicializar OpenGL.
        juego.autoX = 12; // Simula que el auto se desplazó horizontalmente.
        juego.autoZ = 4; // Simula un desplazamiento en profundidad.
        juego.velocidad = 10; // Simula que el auto está avanzando.
        juego.angulo = 2; // Simula que el vehículo cambió de orientación.
        juego.reiniciar(); // Ejecuta el mismo reinicio que se activa con R.
        assertEquals(-30f, juego.autoX, 0f); // Exige recuperar la coordenada X inicial.
        assertEquals(30f, juego.autoZ, 0f); // Exige recuperar la coordenada Z inicial.
        assertEquals(0f, juego.velocidad, 0f); // Exige que el vehículo quede detenido.
        assertEquals(0f, juego.angulo, 0f); // Exige recuperar la dirección frontal inicial.
        assertEquals(0f, juego.chataAngulo, 0f); // Exige que la chata vuelva a quedar alineada.
        assertEquals(30f + clase2.ENGANCHE + clase2.LANZA, juego.chataZ, 0.001f); // Exige la chata justo detrás del auto.
    }
}
