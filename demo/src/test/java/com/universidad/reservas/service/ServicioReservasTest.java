package com.universidad.reservas.service;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals; // Se usará en Bloque 3
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.universidad.reservas.model.Habitacion;
import com.universidad.reservas.model.Reserva;
import com.universidad.reservas.model.TipoHabitacion;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║ TALLER: Pruebas Unitarias con JUnit 5 y Mockito ║
 * ║ Sistema de Reservas de Hotel ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║ INSTRUCCIONES: ║
 * ║ 1. Lee cada bloque @Test y su // TODO con atención. ║
 * ║ 2. Implementa el cuerpo de cada prueba siguiendo el patrón ║
 * ║ AAA (Arrange - Act - Assert). ║
 * ║ 3. Ejecuta las pruebas con: ./gradlew test ║
 * ║ 4. Todas las pruebas deben pasar en VERDE al terminar. ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ServicioReservas - Taller de Pruebas Unitarias")
class ServicioReservasTest {

    // ─── Dependencias mockeadas ───────────────────────────────────────
    @Mock
    private PasarelaPago pasarelaPago;

    @Mock
    private ServicioNotificacion servicioNotificacion;

    // ─── System Under Test (SUT) ──────────────────────────────────────
    private ServicioReservas servicioReservas;

    // ─── Datos de prueba reutilizables ────────────────────────────────
    private Habitacion habitacionDisponible;
    private Habitacion habitacionNoDisponible;
    private LocalDate hoy;
    private LocalDate manana;

    
    @BeforeEach
    void setUp() {
        // TODO: Inicializa el SUT (servicioReservas) inyectándole los dos mocks.
        // Luego, crea los datos de prueba:
        // - habitacionDisponible: id="H-101", tipo DOBLE, precio 120.0, disponible=true
        // - habitacionNoDisponible: id="H-202", tipo SUITE, precio 250.0,
        // disponible=false
        // - hoy: la fecha de hoy (LocalDate.now())
        // - manana: la fecha de mañana (hoy.plusDays(1))

    MockitoAnnotations.openMocks(this);

    servicioReservas = new ServicioReservas(pasarelaPago, servicioNotificacion);

    
    habitacionDisponible = new Habitacion("H-101", TipoHabitacion.DOBLE, 120.0, true);
    habitacionNoDisponible = new Habitacion("H-202", TipoHabitacion.SUITE, 250.0, false);

    hoy = LocalDate.now();
    manana = hoy.plusDays(1);

    }

    // ═══════════════════════════════════════════════════════════════════
    // BLOQUE 1: Camino feliz (Happy Path)
    // ═══════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Camino feliz - Reservas exitosas")
    class CaminoFeliz {

        @Test
        @DisplayName("Debe crear una reserva exitosa para una estancia de 3 noches")
        void debeCrearReservaExitosaParaTresNoches() {
            // TODO: Implementa esta prueba siguiendo el patrón AAA.
            //
            // ARRANGE:
            // - Configura el mock de pasarelaPago para que retorne true
            // cuando se llame procesarCobro() con eq("Carlos Pérez") y eq(360.0)
            // (3 noches × $120.0/noche = $360.0).
            // Pista: usa when(pasarelaPago.procesarCobro(eq(...),
            // eq(...))).thenReturn(true)
            //
            // ACT:
            // - Llama a servicioReservas.crearReserva() con:
            // huesped="Carlos Pérez", habitacionDisponible, hoy, hoy.plusDays(3)
            //
            // ASSERT:
            // - assertNotNull(reserva)
            // - assertEquals("Carlos Pérez", reserva.huesped())
            // - assertEquals(360.0, reserva.totalPagado())
            // - assertNotNull(reserva.codigoReserva())

    
            String huesped = "Carlos Pérez";
            double montoEsperado = 3 * 20.0;

            when(pasarelaPago.procesarCobro(eq(huesped), eq(montoEsperado)))
                .thenReturn(true);

           
            Reserva reserva = servicioReservas.crearReserva(
                huesped,
                habitacionDisponible,
                hoy,
                hoy.plusDays(3)
            );

            assertNotNull(reserva, "La reserva no debe ser null");
            assertEquals(huesped, reserva.huesped(), "El huésped debe coincidir");
            assertEquals(montoEsperado, reserva.totalPagado(), 0.001, "El monto total debe ser correcto");
            assertNotNull(reserva.codigoReserva(), "Debe generar un código de reserva");
        
        }

        @Test
        @DisplayName("Debe aplicar 10% de descuento para estancias de 7+ noches")
        void debeAplicarDescuentoParaEstanciaLarga() {
            // TODO: Implementa esta prueba.
            //
            // ARRANGE:
            // - El total SIN descuento sería: 7 noches × $120.0 = $840.0
            // - Con 10% de descuento: $840.0 - $84.0 = $756.0
            // - Configura el mock de pasarelaPago para que acepte el cobro de $756.0
            // Pista: when(pasarelaPago.procesarCobro(eq("Ana López"),
            // eq(756.0))).thenReturn(true)
            
            when(pasarelaPago.procesarCobro(eq("Ana López"), eq(756.0)))
                .thenReturn(true);


            // ACT:
            // - Crea una reserva para "Ana López", habitacionDisponible,
            // desde hoy hasta hoy.plusDays(7)
            
            Reserva reserva = servicioReservas.crearReserva("Ana López", habitacionDisponible, hoy, hoy.plusDays(7));
            
            // ASSERT:
            // - Verifica que el totalPagado sea exactamente 756.0

            assertNotNull(reserva, "La reserva no debe ser null");
            assertEquals(756.0, reserva.totalPagado(), 0.001, "El monto total debe ser correcto");
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // BLOQUE 2: Manejo de errores (Excepciones)
    // ═══════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Manejo de errores - Excepciones esperadas")
    class ManejoErrores {

        @Test
        @DisplayName("Debe lanzar IllegalStateException si la habitación NO está disponible")
        void debeLanzarExcepcionSiHabitacionNoDisponible() {
            // TODO: Implementa esta prueba.
            //
            // NOTA: No se necesita ARRANGE porque habitacionNoDisponible ya fue
            // configurada en setUp() y la excepción se lanza antes de interactuar
            // con la pasarela de pago, por lo que no se requiere configurar mocks.
            //
            // ACT & ASSERT:
            // - Usa assertThrows(IllegalStateException.class, () -> { ... })
            // - Dentro del lambda, llama a crearReserva con la habitacionNoDisponible
            // - Verifica que el mensaje de la excepción contiene "no está disponible"

            String noDisponible = "La habitación no está disponible";

            IllegalStateException excepcion = assertThrows(
            IllegalStateException.class, () -> servicioReservas.crearReserva(

                "Camilo Ardila", 
                habitacionNoDisponible, 
                hoy, 
                hoy.plusDays(5)),
                noDisponible
            );

            assertTrue(excepcion.getMessage().contains(noDisponible));


            // Pista: assertThrows retorna la excepción lanzada.
            // Puedes hacer: var ex = assertThrows(...);
            // assertTrue(ex.getMessage().contains("..."))
        }

        @Test
        @DisplayName("Debe lanzar IllegalArgumentException si la fecha de salida no es posterior a la de entrada")
        void debeLanzarExcepcionSiFechasSonInvalidas() {
            // TODO: Implementa esta prueba.
            //
            // ACT & ASSERT:
            // - Llama a crearReserva con fechaEntrada = hoy y fechaSalida = hoy
            // (misma fecha, no es posterior)

            IllegalArgumentException excepcion = assertThrows(
                IllegalArgumentException.class, () -> servicioReservas.crearReserva(
                
                "Camilo Ardila", 
                habitacionDisponible, 
                hoy, 
                hoy),
                "fecha de salida"
            
            );

            assertTrue(excepcion.getMessage().contains("fecha de salida"));

            // - Debe lanzar IllegalArgumentException
            // - Verifica que el mensaje contiene "fecha de salida"
        }

        @Test
        @DisplayName("Debe lanzar IllegalStateException cuando el pago es rechazado")
        void debeLanzarExcepcionSiPagoRechazado() {
            // TODO: Implementa esta prueba.
            //
            // ARRANGE:
            // - Configura el mock de pasarelaPago para que retorne FALSE
            // (simula un pago rechazado).
            // Pista: when(pasarelaPago.procesarCobro(anyString(),
            // anyDouble())).thenReturn(false)

            when(pasarelaPago.procesarCobro(anyString(), anyDouble())).thenReturn(false);
        
            // ACT & ASSERT:
            // - Llama a crearReserva con habitacionDisponible, hoy, manana
            // - Verifica que lanza IllegalStateException
            // - Verifica que el mensaje contiene "rechazado"

            IllegalStateException excepcion = assertThrows(
                IllegalStateException.class, () -> servicioReservas.crearReserva(
                    "Camilo Ardila", 
                    habitacionDisponible, 
                    hoy, 
                    manana),
                    "rechazado"
            );

            assertTrue(excepcion.getMessage().contains("rechazado"));

        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // BLOQUE 3: Verificación de interacciones (verify)
    // ═══════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Verificación de interacciones con mocks")
    class VerificacionInteracciones {

        @Test
        @DisplayName("Debe enviar notificación exactamente UNA vez después de una reserva exitosa")
        void debeEnviarNotificacionUnaVez() {
            // TODO: Implementa esta prueba.
            //
            // ARRANGE:
            // - Configura pasarelaPago para que acepte el cobro
            
            when(pasarelaPago.procesarCobro(anyString(), anyDouble())).thenReturn(true);

            // ACT:
            // - Crea una reserva exitosa
            
            Reserva reserva = servicioReservas.crearReserva(
                "Camilo Ardila",
                habitacionDisponible,
                hoy,
                hoy.plusDays(2)
            );


            // ASSERT con verify():
            // - Usa verify(servicioNotificacion,
            // times(1)).enviarConfirmacion(any(Reserva.class))
            // para confirmar que la notificación se envió exactamente 1 vez

            verify(servicioNotificacion, times(1)).enviarConfirmacion(any(Reserva.class));
        }

        @Test
        @DisplayName("NO debe enviar notificación si el pago fue rechazado")
        void noDebeEnviarNotificacionSiPagoFalla() {
            // TODO: Implementa esta prueba.
            //
            // ARRANGE:
            // - Configura pasarelaPago para que RECHACE el cobro (retorne false)
            
            when(pasarelaPago.procesarCobro(anyString(), anyDouble())).thenReturn(false);

            // ACT:
            // - Intenta crear una reserva (usa assertThrows porque lanzará excepción)
            

            assertThrows(IllegalStateException.class, () -> servicioReservas.crearReserva(
                "Camilo Ardila",
                habitacionDisponible,
                hoy,
                hoy.plusDays(2)
            ));

            // ASSERT con verify():
            // - Usa verify(servicioNotificacion, never()).enviarConfirmacion(any())
            // para confirmar que NUNCA se llamó a enviarConfirmacion

            verify(servicioNotificacion, times(0)).enviarConfirmacion(any());
        }

        @Test
        @DisplayName("Debe llamar a procesarCobro con el monto correcto (sin descuento)")
        void debeCobraMontoCorrectoSinDescuento() {
            // TODO: Implementa esta prueba.
            //
            // ARRANGE:
            // - Configura pasarelaPago para aceptar cualquier cobro
            
            when(pasarelaPago.procesarCobro(anyString(), anyDouble())).thenReturn(true);

            // ACT:
            // - Crea una reserva de 2 noches (2 × $120 = $240)
            
            Reserva reserva = servicioReservas.crearReserva(
                "Luis García",
                habitacionDisponible,
                hoy,
                hoy.plusDays(2)
            );

            // ASSERT con verify():
            // - verify(pasarelaPago).procesarCobro("Luis García", 240.0)
            // - Esto verifica que se llamó al mock con los argumentos EXACTOS esperados

            verify(pasarelaPago).procesarCobro(eq("Luis García"), eq(240.0));
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // BLOQUE 4: Prueba del cálculo interno (BONUS)
    // ═══════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Bonus - Cálculo del total")
    class CalculoTotal {

        @Test
        @DisplayName("calcularTotal: sin descuento para menos de 7 noches")
        void calcularTotalSinDescuento() {
            // TODO: Implementa esta prueba SIN mocks (es lógica pura).
            //
            // ACT:
            // - Llama directamente a servicioReservas.calcularTotal(100.0, 5)
            
                double total = servicioReservas.calcularTotal(100.0, 5);

            // ASSERT:
            // - assertEquals(500.0, total)

            assertEquals(500, total);
        };

        @Test
        @DisplayName("calcularTotal: con descuento del 10% para 7+ noches")
        void calcularTotalConDescuento() {
            // TODO: Implementa esta prueba SIN mocks (es lógica pura).
            //
            // ACT:
            // - Llama a servicioReservas.calcularTotal(200.0, 10)
            // - Sin descuento sería: 200 × 10 = 2000
            // - Con descuento 10%: 2000 - 200 = 1800
            
            double total = servicioReservas.calcularTotal(200.0, 10);


            // ASSERT:
            // - assertEquals(1800.0, total)

            assertEquals(1800, total);
            assertTrue(!eq(20000 != total));
        }
    }
}
