
import Bulding.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.Timer;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;


public class GamePanel extends JPanel implements MouseListener, MouseMotionListener, ActionListener, KeyListener {

    private GameState gameState;
    private JFrame frame; // Referencja do głównego okna
    private ArrayList<Explosion> explosions; // Lista eksplozji
    private ArrayList<Soldier> soldiers;
    private ArrayList<Cryopit> cryopits;
    private ArrayList<Minigunner> minigunners;
    private ArrayList<BattleVehicle> battleVehicles;
    private ArrayList<Artylery> artylerys;
    private ArrayList<Soldier> selectedSoldiers;
    private ArrayList<Harvester> harvesters;
    private ArrayList<Enemy> enemies;
    private ArrayList<EnemyShooter> enemyShooters;
    private ArrayList<BuilderVehicle> builderVehicles;
    private ArrayList<EnemyToo> enemiesToo; // Nowa lista dla EnemyToo
    private ArrayList<EnemyHunter> enemyHunters;
    private ArrayList<Bullet> bullets; // Lista pocisków
    private ArrayList<MinigunnerBullet> minigunnerBullets;
    private ArrayList<ArtBullet> artBullets;
    private ArrayList<Object> occupiedTargets = new ArrayList<>();
    private ArrayList<Baracks> baracks;
    private ArrayList<Factory> factories;
    private ArrayList<Bulding> buldings;
    private Soldier selectedSoldier;
    private Minigunner selectedMinigunner;
    private BattleVehicle selectedBattleVehicle;
    private Artylery selectedArtylery;
    private Harvester selectedHarvester;
    private BuilderVehicle selectedBuilderVehicle;
    private Baracks selectedBaracks;
    private Factory selectedFactories;

    private Soldier soldier; // to jest do zapisywania do calego Soldier w savegame i load

    private Timer movementTimer;
    private Timer shootingTimer;
    private Timer shootingTimer2;
    private Timer enemyShootingTimer;
    private ArrayList<Projectile> projectiles;
    private ArrayList<ResourcesSteel> resources;
    private ArrayList<PowerPlant> powerPlants;
    private int collectedSteel = 10000; // Przechowuje zebraną ilość stali
    private int totalPower = 0; // Całkowity dostępny power
    private JButton btnPowerPlant;
    private JButton btnBaracks;
    private JButton btnFactory;
    private JButton btnSoldier;
    private JButton btnHarvester;
    private JButton btnBattleVehicle;
    private JButton btnArtylery;

    private ArrayList<Hive> hives;
    private BufferedImage backgroundImage;

    private long lastSpawnTime = System.currentTimeMillis();
    private static final long SPAWN_INTERVAL = 45000; // resoawn przeciwnika w milisekundach - pojawiaja sie od prawej strony mapy
    private Timer timer;
    private long previousTime = System.currentTimeMillis();



    private boolean showBuilderMenu = false;
    private boolean showBaracksMenu = false;
    private boolean showFactorysMenu = false;

    // to jest odpowiedzalne za to by nie pojawila sie jakas jednostka na sobie

    private boolean isCollidingWithOthers(int x, int y, ArrayList<Soldier> soldiers, ArrayList<Minigunner> minigunners, ArrayList<BattleVehicle> battleVehicles) {
        Rectangle newSoldierBounds = new Rectangle(x, y, 42, 34); // Wymiary żołnierza
        Rectangle newMinigunerBounds = new Rectangle(x, y, 20, 20);
        Rectangle newBattleVehicle = new Rectangle(x, y, 50,50);

        for (Soldier soldier : soldiers) {
            if (newSoldierBounds.intersects(soldier.getBounds())) {
                return true; // Koliduje z innym żołnierzem
            }
        }
        for (Minigunner minigunner : minigunners){
            if (newMinigunerBounds.intersects(minigunner.getBounds())){
                return true;
            }
        }

        for (BattleVehicle battleVehicle : battleVehicles) {
            if (newBattleVehicle.intersects(battleVehicle.getBounds())){
                return true;
            }
        }
        return false; // Pozycja jest wolna
    }



   /////////// do zaznaczania grupowego
    private Rectangle selectionRectangle = null; // Prostokąt zaznaczenia
    private Point startPoint = null; // Punkt początkowy zaznaczenia

    // to  jest od coraz większego respu enemytoo
    private int waveNumber = 1; // Zaczynamy od fali nr 1
    private int baseEnemyCount = 2; // Początkowa liczba wrogów, tu musisz ustawic ile ich na poczatek sie pojawia podczas respa



    public GamePanel(JFrame frame) {
// tu jest okenko glowne gry oraz okienko ktore jest po nacisnieciu ecape
        {this.frame = frame;
            this.setPreferredSize(new Dimension(3000, 3000)); // Duża mapa

            setFocusable(true);
            requestFocusInWindow();

            // Obsługa klawisza ESC
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        showPauseMenu();
                    }
                }
            });
        }



        {
            // Ustawiamy większy rozmiar mapy
            this.setPreferredSize(new Dimension(3000, 3000));  // Zwiększamy rozmiar mapy
        }

        addKeyListener(this);
        setFocusable(true); // Wymagane do odbierania zdarzeń klawiatury

        try {
            // Wczytaj obraz tła z plikuF:\projekty JAVA
            backgroundImage = ImageIO.read(new File("F:/projekty JAVA/TerraBionX2/plener.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Nie udało się załadować obrazu tła.");
        }

        this.hives = new ArrayList<>();
        this.cryopits = new ArrayList<>();
        this.powerPlants = new ArrayList<>();
        this.soldiers = new ArrayList<>();
        this.minigunners = new ArrayList<>();
        this.battleVehicles = new ArrayList<>();
        this.artylerys = new ArrayList<>();
        this.builderVehicles = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.enemyShooters = new ArrayList<>();
        this.harvesters = new ArrayList<>();
        this.enemiesToo = new ArrayList<>();
        this.enemyHunters = new ArrayList<>();
        this.resources = new ArrayList<>();
        this.bullets = new ArrayList<>();
        this.minigunnerBullets = new ArrayList<>();
        this.artBullets = new ArrayList<>();
        this.selectedSoldier = null;
        this.selectedMinigunner = null;
        this.selectedSoldiers = new ArrayList<>();
        this.selectedBattleVehicle = null;
        this.selectedArtylery = null;
        this.selectedHarvester = null;
        this.selectedBuilderVehicle = null;
        this.selectedBaracks = null;
        this.selectedFactories = null;
        this.explosions = new ArrayList<>();

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.projectiles = new ArrayList<>();
        this.baracks = new ArrayList<>();
        this.factories = new ArrayList<>();
        this.buldings = new ArrayList<>();


        timer = new Timer(1000 / 60, this); // Wywołanie actionPerformed co ~16 ms
        timer.start();


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// tu masz przyciski gdy zaznaczysz Buldiera
        setLayout(null); // Ustawienie ręcznego układu dla dodania przycisków

        btnSoldier = new JButton("soldier");
        btnSoldier.setBounds(10, 50, 120, 30); // Pozycja i rozmiar
        btnSoldier.setVisible(false); // Ukryj przycisk na starcie
        add(btnSoldier);


        btnSoldier.addActionListener(e -> {
            if (selectedBaracks != null) {
                if (collectedSteel >= 1000) {
                    // Oblicz pozycję jednostki obok Baracks
                    int soldierX = selectedBaracks.getX() + 90;
                    int soldierY = selectedBaracks.getY();

                    // Dodaj jednostkę Soldier
                    soldiers.add(new Soldier(soldierX, soldierY));
                    // Zmniejsz stal o 500 jednostek
                    collectedSteel -= 1000;
                    System.out.println("Dodano Soldier.");
                    // Ukryj menu barakow
                    showBaracksMenu = false;
                    updateBaracksMenu();
                    repaint(); // Odśwież panel
                }
                else {
                        // Poinformuj gracza, że nie ma wystarczającej ilości stali
                        System.out.println("Nie masz wystarczającej ilości stali! Potrzebujesz 1000 Steel.");
                    }
            }
        });

        setLayout(null);

        btnHarvester = new JButton("Harvester");
                btnHarvester.setBounds(10, 50, 120, 30); // Pozycja i rozmiar
        btnHarvester.setVisible(false);
        add(btnHarvester);

        btnArtylery = new JButton("Artylery");
            btnArtylery.setBounds(10, 90, 120, 30);
            btnArtylery.setVisible(false);
            add(btnArtylery);

        btnBattleVehicle= new JButton("Armored Vehicle");
        btnBattleVehicle.setBounds(10, 130, 120, 30); // Pozycja i rozmiar
        btnBattleVehicle.setVisible(false);
        add(btnBattleVehicle);


        btnHarvester.addActionListener(e -> {
            if (selectedFactories != null) {
                if (collectedSteel >= 2000) {
                    // Oblicz pozycję jednostki obok Baracks
                    int harvesterX = selectedFactories.getX() + 50;
                    int harvesterY = selectedFactories.getY();

                    // Dodaj jednostkę herv
                    harvesters.add(new Harvester(harvesterX, harvesterY));
                    // Zmniejsz stal o 500 jednostek
                    collectedSteel -= 2000;
                    System.out.println("Dodano harvester.");
                    // Ukryj menu factory
                    showFactorysMenu = false;
                    updateFactorysMenu();
                    repaint(); // Odśwież panel
                }
                else {
                    // Poinformuj gracza, że nie ma wystarczającej ilości stali
                    System.out.println("Nie masz wystarczającej ilości stali! Potrzebujesz 2000 Steel.");
                }
            }
        });

        setLayout(null);

        btnArtylery.addActionListener(e -> {
            if (selectedFactories != null) {
                if (collectedSteel >= 2000) {
                    // Oblicz pozycję jednostki obok Factory
                    int artyleryX = selectedFactories.getX() + 50;
                    int artyleryY = selectedFactories.getY();

                    // Dodaj jednostkę arty
                    artylerys.add(new Artylery(artyleryX, artyleryY));
                    // Zmniejsz stal o 500 jednostek
                    collectedSteel -= 2000;
                    System.out.println("Dodano artylery.");
                    // Ukryj menu factory
                    showFactorysMenu = false;
                    updateFactorysMenu();
                    repaint(); // Odśwież panel
                }
                else {
                    // Poinformuj gracza, że nie ma wystarczającej ilości stali
                    System.out.println("Nie masz wystarczającej ilości stali! Potrzebujesz 2000 Steel.");
                }
            }
        });
        setLayout(null);

        btnBattleVehicle.addActionListener(e -> {
            if (selectedFactories != null) {
                if (collectedSteel >= 2000) {
                    // Oblicz pozycję jednostki obok Factory
                    int battleVehicleX = selectedFactories.getX() + 50;
                    int battleVehicleY = selectedFactories.getY();

                    // Dodaj jednostkę battlevehicle
                    battleVehicles.add(new BattleVehicle(battleVehicleX, battleVehicleY));
                    // Zmniejsz stal o 2000 jednostek
                    collectedSteel -= 2000;
                    System.out.println("Dodano Armored Vehicle.");
                    // Ukryj menu factory
                    showFactorysMenu = false;
                    updateFactorysMenu();
                    repaint(); // Odśwież panel
                }
                else {
                    // Poinformuj gracza, że nie ma wystarczającej ilości stali
                    System.out.println("Nie masz wystarczającej ilości stali! Potrzebujesz 2000 Steel.");
                }
            }
        });

//        btnRocketer.addActionListener(e -> {
//            if (selectedBaracks != null) {
//                System.out.println("Dodano Rocketer (przyszła implementacja).");
//                // Logika dla Rocketer zostanie dodana później
//            }
//        });
//////////////////////////////////////////// przyciski do budowania budynkow u buldiera //////////////

        btnPowerPlant = new JButton("Power Plant");
        btnPowerPlant.setBounds(10, 50, 120, 30); // Pozycja i rozmiar
        btnPowerPlant.setVisible(false); // Ukryj przycisk na starcie
        add(btnPowerPlant);

        btnBaracks = new JButton("Barracks");
        btnBaracks.setBounds(10, 90, 120, 30);
        btnBaracks.setVisible(false);
        add(btnBaracks);

        btnFactory = new JButton("Factory");
        btnFactory.setBounds(10, 130, 120, 30);
        btnFactory.setVisible(false);
        add(btnFactory);

        // Dodaj logikę dla każdego przycisku
        btnPowerPlant.addActionListener(e -> {
            if (selectedBuilderVehicle != null) {
                if (collectedSteel >= 1000) {
                    // Oblicz pozycję elektrowni obok BuilderVehicle
                    int plantX = selectedBuilderVehicle.getX() + 50;
                    int plantY = selectedBuilderVehicle.getY();

                    // Utwórz prostokąt dla nowego budynku
                    Rectangle newBuilding = new Rectangle(plantX, plantY, 50, 50);

                    // Sprawdź kolizję z innymi budynkami
                    boolean collision = false;
                    for (PowerPlant plant : powerPlants) {
                        if (plant.getBounds().intersects(newBuilding)) {
                            collision = true;
                            break;
                        }
                    }
                    for (Baracks barack : baracks) {
                        if (barack.getBounds().intersects(newBuilding)) {
                            collision = true;
                            break;
                        }
                    }
                    // Jeśli nie ma kolizji, dodaj nowy budynek
                    if (!collision) {
                        powerPlants.add(new PowerPlant(plantX, plantY));
                        collectedSteel -= 1000;
                        totalPower += PowerPlant.getPowerGenerated();
                        System.out.println("Dodano Power Plant. Prąd: " + totalPower + ", Stal: " + collectedSteel);
                        repaint();
                    } else {
                        System.out.println("Nie można postawić budynku tutaj! Miejsce zajęte.");
                    }
                } else {
                    System.out.println("Nie masz wystarczającej ilości stali! Potrzebujesz 1000 Steel.");
                }
            }
        });
        //przycisk w menu jak zrobisz baraki
        btnBaracks.addActionListener(e -> {
            if (selectedBuilderVehicle != null) {
                // Sprawdź, czy gracz ma wystarczającą ilość stali i prądu
                if (collectedSteel >= 1500 && totalPower >= 100) {
                    // Oblicz pozycję koszar obok BuilderVehicle
                    int baracksX = selectedBuilderVehicle.getX() + 50; // 50 pikseli na prawo
                    int baracksY = selectedBuilderVehicle.getY();

                    // Utwórz prostokąt dla nowych koszar
                    Rectangle newBuilding = new Rectangle(baracksX, baracksY, 50, 50);

                    // Sprawdź kolizję z innymi budynkami
                    boolean collision = false;
                    for (PowerPlant plant : powerPlants) {
                        if (plant.getBounds().intersects(newBuilding)) {
                            collision = true;
                            break;
                        }
                    }
                    for (Baracks barack : baracks) {
                        if (barack.getBounds().intersects(newBuilding)) {
                            collision = true;
                            break;
                        }
                    }

                    // Jeśli nie ma kolizji, dodaj nowe koszary
                    if (!collision) {
                        baracks.add(new Baracks(baracksX, baracksY));

                        // Odejmij zasoby
                        collectedSteel -= 1500;
                        totalPower -= 100;

                        System.out.println("Dodano Baracks. Stal: " + collectedSteel + ", Prąd: " + totalPower);

                        // Ukryj menu budowy i odśwież panel
                        showBuilderMenu = false;
                        updateBuilderMenu();
                        repaint(); // Odśwież panel
                    } else {
                        System.out.println("Nie można postawić koszar tutaj! Miejsce zajęte.");
                    }
                } else {
                    // Poinformuj gracza, że nie ma wystarczających zasobów
                    System.out.println("Nie masz wystarczających zasobów! Potrzebujesz 1500 Steel i 100 Power.");
                }
            }
        });
        //przycisk do robienia Factory/
        btnFactory.addActionListener(e -> {
            if (selectedBuilderVehicle != null) {
                // Sprawdź, czy gracz ma wystarczającą ilość stali i prądu
                if (collectedSteel >= 2500 && totalPower >= 100) {
                    // Oblicz pozycję koszar obok BuilderVehicle
                    int factoriesX = selectedBuilderVehicle.getX() + 50; // 50 pikseli na prawo
                    int factoriesY = selectedBuilderVehicle.getY();

                    // Dodaj koszary do listy
                    factories.add(new Factory(factoriesX, factoriesY));

                    // Odejmij zasoby
                    collectedSteel -= 2500;
                    totalPower -= 100;

                    System.out.println("Dodano Factory Stal: " + collectedSteel + ", Prąd: " + totalPower);

                    // Ukryj menu budowy i odśwież panel
                    showBuilderMenu = false;
                    updateBuilderMenu();
                    repaint(); // Odśwież panel
                } else {
                    // Poinformuj gracza, że nie ma wystarczających zasobów
                    System.out.println("Nie masz wystarczających zasobów! Potrzebujesz 1500 Steel i 100 Power.");
                }
            }
        });


/////////////////////////////////////////////////////////////////////////////////////////
        // Dodajemy 4 losowe pola Stalil Resources Steel
        Random rand = new Random();
        for (int i = 0; i < 13; i++) {
            int x = rand.nextInt(1800); // Losowa pozycja X na mapie
            int y = rand.nextInt(850); // Losowa pozycja Y na mapie
            resources.add(new ResourcesSteel(x, y));
        }
        /////////////////////////////////// tu dodaje hives//// od prawej strony
        for (int i = 0; i < 3; i++) {
            int x = 920 + rand.nextInt(811); // Losowa pozycja na mapie w zakresie od 920 pxl + 511
            int y = rand.nextInt(850); // Losowa pozycja Y na mapie
            hives.add(new Hive(x, y));
//                int x = 1620 + rand.nextInt(111); // Pozycja na prawej krawędzi poza mapą (poza szerokością 1600)
//                int y = rand.nextInt(900); // Losowa pozycja na osi Y w granicach wysokości mapy (0-900)
        }
        for (int i = 0; i < 1; i++){
            int x = 920 + rand.nextInt(811); // Losowa pozycja na mapie w zakresie od 920 pxl + 511
            int y = rand.nextInt(850); // Losowa pozycja Y na mapie
            cryopits.add(new Cryopit(x, y));
        }




        // TU  masz ilosc żołnierzy pojawiajacych sie losowo na start
        for (int i = 0; i < 10; i++) {
            int maxAttempts = 100; // Ograniczenie prób, by uniknąć nieskończonej pętli
            int attempts = 0;
            int x, y;

            do {
                // Środek mapy jako prostokąt obejmujący 50% szerokości i wysokości
                int centerXStart = 700 / 4; // 25% szerokości od lewej
                int centerXEnd = 3 * 700 / 4; // 75% szerokości
                int centerYStart = 500 / 4; // 25% wysokości od góry
                int centerYEnd = 3 * 500 / 4; // 75% wysokości

                // Losowa pozycja w obrębie środkowej części
                x = rand.nextInt(centerXEnd - centerXStart) + centerXStart;
                y = rand.nextInt(centerYEnd - centerYStart) + centerYStart;

                attempts++;
            } while (isCollidingWithOthers(x, y, soldiers, minigunners, battleVehicles) && attempts < maxAttempts);

            if (attempts < maxAttempts) {
                soldiers.add(new Soldier(x, y)); // Dodanie żołnierza, jeśli znaleziono wolne miejsce
            } else {
                System.out.println("Nie znaleziono wolnego miejsca dla nowego żołnierza!");
            }
        }
//        for (int i = 0; i < 10; i++) {int side = rand.nextInt(4);  // Losujemy, z której strony pojawi się przeciwnik
//            int x, y;
//
//// Środek mapy jako prostokąt obejmujący 50% szerokości i wysokości
//            int centerXStart = 700 / 4; // 25% szerokości od lewej
//            int centerXEnd = 3 * 700 / 4; // 75% szerokości
//            int centerYStart = 500 / 4; // 25% wysokości od góry
//            int centerYEnd = 3 * 500 / 4; // 75% wysokości
//
//// Losowa pozycja w obrębie środkowej części
//            x = rand.nextInt(centerXEnd - centerXStart) + centerXStart;
//            y = rand.nextInt(centerYEnd - centerYStart) + centerYStart;
//
//// Dodaj nowego żołnierza w środkowej części mapy
//            soldiers.add(new Soldier(x, y));
//        }
        for (int i = 0; i < 10; i++) {
            int maxAttempts = 100; // Ograniczenie prób, by uniknąć nieskończonej pętli
            int attempts = 0;
            int x, y;

            do {
                // Środek mapy jako prostokąt obejmujący 50% szerokości i wysokości
                int centerXStart = 700 / 4; // 25% szerokości od lewej
                int centerXEnd = 3 * 700 / 4; // 75% szerokości
                int centerYStart = 500 / 4; // 25% wysokości od góry
                int centerYEnd = 3 * 500 / 4; // 75% wysokości

                // Losowa pozycja w obrębie środkowej części
                x = rand.nextInt(centerXEnd - centerXStart) + centerXStart;
                y = rand.nextInt(centerYEnd - centerYStart) + centerYStart;

                attempts++;
            } while (isCollidingWithOthers(x, y, soldiers, minigunners, battleVehicles) && attempts < maxAttempts);

            if (attempts < maxAttempts) {
                minigunners.add(new Minigunner(x, y)); // Dodanie żołnierza, jeśli znaleziono wolne miejsce
            } else {
                System.out.println("Nie znaleziono wolnego miejsca dla nowego żołnierza!");
            }
            //tu masz resp czołgi
        }
        for (int i = 0; i < 10; i++) {
            int maxAttempts = 100; // Ograniczenie prób, by uniknąć nieskończonej pętli
            int attempts = 0;
            int x, y;

            do {
                // Środek mapy jako prostokąt obejmujący 50% szerokości i wysokości
                int centerXStart = 700 / 4; // 25% szerokości od lewej
                int centerXEnd = 3 * 700 / 4; // 75% szerokości
                int centerYStart = 500 / 4; // 25% wysokości od góry
                int centerYEnd = 3 * 500 / 4; // 75% wysokości

                // Losowa pozycja w obrębie środkowej części
                x = rand.nextInt(centerXEnd - centerXStart) + centerXStart;
                y = rand.nextInt(centerYEnd - centerYStart) + centerYStart;

                attempts++;
            } while (isCollidingWithOthers(x, y, soldiers, minigunners, battleVehicles) && attempts < maxAttempts);
            if (attempts < maxAttempts) {
                battleVehicles.add(new BattleVehicle(x, y)); // Dodanie żołnierza, jeśli znaleziono wolne miejsce
            }
            else {
                System.out.println("Nie znaleziono wolnego miejsca dla nowego żołnierza!");
            }
        }
//        for (int i = 0; i < 7; i++) {
//            battleVehicles.add(new BattleVehicle(rand.nextInt(600), rand.nextInt(400)));
//        }



//        for (int i = 0; i < 10; i++) {int side = rand.nextInt(4);  // Losujemy, z której strony pojawi się
//            int x, y;
//
//// Środek mapy jako prostokąt obejmujący 50% szerokości i wysokości
//            int centerXStart = 700 / 4; // 25% szerokości od lewej
//            int centerXEnd = 3 * 700 / 4; // 75% szerokości
//            int centerYStart = 500 / 4; // 25% wysokości od góry
//            int centerYEnd = 3 * 500 / 4; // 75% wysokości
//
//// Losowa pozycja w obrębie środkowej części
//            x = rand.nextInt(centerXEnd - centerXStart) + centerXStart;
//            y = rand.nextInt(centerYEnd - centerYStart) + centerYStart;
//
//// Dodaj nowego żołnierza w środkowej części mapy
//            minigunners.add(new Minigunner(x, y));
//        }

        for (int i = 0; i < 2; i++) {
            builderVehicles.add(new BuilderVehicle(rand.nextInt(600), rand.nextInt(400)));
        }


        for (int i = 0; i < 1; i++) {
            int x = 920 + rand.nextInt(511); // Losowa pozycja na mapie w zakresie od 920 pxl + 511
            int y = rand.nextInt(850); // Losowa pozycja Y na mapie
            enemyHunters.add(new EnemyHunter(x, y));
        }
        for (int i = 0; i < 4; i++) {
            artylerys.add(new Artylery(rand.nextInt(600), rand.nextInt(400)));
        }

        // Dodajemy 3 losowych wrogów
        for (int i = 0; i < 1; i++) {
            int x = 920 + rand.nextInt(511); // Losowa pozycja na mapie w zakresie od 920 pxl + 511
            int y = rand.nextInt(850); // Losowa pozycja Y na mapie
            enemies.add(new Enemy(x, y));
        }
        for (int i = 0; i < 1; i++){
            enemyShooters.add(new EnemyShooter(rand.nextInt(1550), rand.nextInt(850)));
        }

        for (int i = 0; i< 2; i++){
            harvesters.add(new Harvester(rand.nextInt(700), rand.nextInt(500)));
        }
        // inwazja
// to dodaje w losowych miejscach na krawedzi mapy EnemyToo
        for (int i = 0; i < 1; i++) {
            int side = rand.nextInt(4);  // Losujemy, z której strony pojawi się przeciwnik
            int x = 0, y = 0;

//            if (side == 0) {  // Górna część
//                x = rand.nextInt(1400);  // Losowa pozycja na osi X w granicach szerokości planszy
//                y = -20;  // Pozycja na górze poza mapą
//            }
           ////// tu poniżej musi być else if/////////////////////
             if (side == 1) {  // Dolna część
                x = rand.nextInt(1400);  // Losowa pozycja na osi X w granicach szerokości planszy
                y = 520;  // Pozycja na dole poza mapą
            } else if (side == 2) {  // Lewa część
                x = -20;  // Pozycja na lewej krawędzi poza mapą
                y = rand.nextInt(800);  // Losowa pozycja na osi Y w granicach wysokości planszy
            }
              if (side == 3) {  // Prawa część
                x = 720;  // Pozycja na prawej krawędzi poza mapą
                y = rand.nextInt(800);  // Losowa pozycja na osi Y w granicach wysokości planszy
            }

            enemiesToo.add(new EnemyToo(x, y));
        }

        // Timer do płynnego ruchu/////////////////////////////////////////////////////////////////////////////////////////////////
        Timer movementTimer = new Timer(40, e -> {
            moveSoldiers();
            moveMinigunner();
            moveBattleVehicle();
            moveHarvesters();
            moveBuilderVeicle();
            moveArtylery();
            updateGame();
            shootEnemiesart();
            shootEnemies();
            minigunnerBulletshoot();
            updateProjectiles();
            updateCryopits(); // Dodane do obsługi rozrostu Cryopit
        });
        movementTimer.start();

        Timer globalCryopitSpawner = new Timer(30000, e -> spawnRandomCryopit()); // to timer co 30s pojawia sie namapie losy cryopit

// Uruchamiamy timer
        globalCryopitSpawner.start();

// Timer dla strzelania wrogów
        enemyShootingTimer = new Timer(700, e -> enemyShoot());
        enemyShootingTimer.start();

//        movementTimer = new Timer(20, e -> moveSoldiers());
//        movementTimer.start();
//
//        movementTimer = new Timer(20, e -> moveHarvesters());
//        movementTimer.start();

        // Timer do strzelania w wrogow
//        shootingTimer = new Timer(10, e -> shootEnemies());
//        shootingTimer.start();

//        shootingTimer2 = new Timer ( 10, e -> shootEnemiesart());
//        shootingTimer2.start();

        // Timer do aktualizacji gry
//        Timer timer = new Timer(30, e -> updateGame()); // to odswierzenie dla updategame, bez tego nie beda poruszac sie EnemyToo w stone Soldier
//        timer.start();


        // Timer dla aktualizacji pocisków
//        Timer projectileUpdateTimer = new Timer(25, e -> updateProjectiles());
//        projectileUpdateTimer.start();

    }
    ///////////// to sa koordynaty postajacach cryopitow
    private void spawnRandomCryopit() {
        Random rand = new Random();

        int x, y;
        do {
            x = rand.nextInt(1920 / 4) * 4; // Losowa pozycja w siatce 4x4
            y = rand.nextInt(980 / 4) * 4;
        }
        while (!Cryopit.isPositionFree(x, y)); // Sprawdzamy, czy miejsce jest wolne

        new Cryopit(x, y); // Tworzymy nowy Cryopit na tej pozycji
        System.out.println("Nowy Cryopit pojawił się na: " + x + ", " + y);
    }

    public void createExplosion(int x, int y) {
        explosions.add(new Explosion(x, y)); // Dodaje nową eksplozję do listy
    }




    // menu buldiera
private void updateBuilderMenu() {
    btnPowerPlant.setVisible(showBuilderMenu);
    btnBaracks.setVisible(showBuilderMenu);
    btnFactory.setVisible(showBuilderMenu);
    repaint(); // Odśwież panel
}


    private void updateBaracksMenu() {
        btnSoldier.setVisible(showBaracksMenu);
        repaint();
    }
    private void stopAllSoldiers() {
        for (Soldier soldier : soldiers) {
            soldier.setTarget(null); // Wyzeruj cel ruchu
        }
        repaint(); // Odśwież ekran, aby odzwierciedlić zmiany
    }

    private void updateFactorysMenu() {
        btnHarvester.setVisible((showFactorysMenu));
        btnArtylery.setVisible((showFactorysMenu));
        btnBattleVehicle.setVisible((showFactorysMenu));
        repaint();
    }
    private void showPauseMenu() {
        JDialog pauseMenu = new JDialog(frame, "Menu Pauzy", true);
        pauseMenu.setSize(300, 150);
        pauseMenu.setLayout(new GridLayout(4, 1));

        JButton resumeButton = new JButton("Wróć do gry");
        JButton saveButton = new JButton("Save Game");
        JButton loadButton = new JButton("Load Game");
        JButton exitButton = new JButton("Wyjdź z gry");

        resumeButton.addActionListener(e -> pauseMenu.dispose());

        // Przycisk zapisu gry
        saveButton.addActionListener(e -> {
            SaveLoadGame.saveGame(soldiers);  // Wywołanie metody zapisu stanu gry
            System.out.println("Gra została zapisana!");
        });

        // Przycisk wczytywania gry
        loadButton.addActionListener(e -> {
            soldiers.clear();  // Czyścimy aktualną listę żołnierzy przed załadowaniem
            soldiers.addAll(SaveLoadGame.loadGame());  // Ładujemy żołnierzy z zapisanej gry
            System.out.println("Gra została wczytana!");
        });

        // Przycisk wyjścia z gry
        exitButton.addActionListener(e -> {
            int confirmation = JOptionPane.showConfirmDialog(frame, "Czy na pewno chcesz wyjść z gry?", "Potwierdzenie wyjścia", JOptionPane.YES_NO_OPTION);
            if (confirmation == JOptionPane.YES_OPTION) {
                System.exit(0);  // Zamyka aplikację
            }
        });

        pauseMenu.add(resumeButton);
        pauseMenu.add(saveButton);
        pauseMenu.add(loadButton);
        pauseMenu.add(exitButton);

        pauseMenu.setLocationRelativeTo(frame);
        pauseMenu.setVisible(true);
    }


//
    private void enemyShoot() {
        for (Enemy enemy : enemies) {
            Projectile projectile = enemy.shootAtNearestSoldier(soldiers);
            if (projectile != null) {
                projectiles.add(projectile);
            }
        }

        for (Enemy enemy : enemies) {
            Projectile projectile = enemy.shootAtNearestArtylery(artylerys);
            if (projectile != null) {
                projectiles.add(projectile);
            }
        }

        for (Enemy enemy : enemies) {
            Projectile projectile = enemy.shootAtNearestBattleVehicle(battleVehicles);
            if (projectile != null) {
                projectiles.add(projectile);
            }
        }


        for (Enemy enemy : enemies) {
            Projectile projectile = enemy.shootAtNearestHarvaester(harvesters);
            if (projectile != null) {
                projectiles.add(projectile);
            }
        }
        for (Enemy enemy : enemies) {
            Projectile projectile = enemy.shootAtNearestBuilder(builderVehicles);
            if (projectile != null) {
                projectiles.add(projectile);
            }
        }
    }
    private long lastCryopitSpawnTime = System.currentTimeMillis();

    private void updateCryopits() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCryopitSpawnTime >= 2000) { // Co 2 sekundy
            for (Cryopit cryopit : cryopits) { // Iterujemy przez istniejące Cryopit
                cryopit.tryToSpawnNewCryopit();
            }
            lastCryopitSpawnTime = currentTime;
        }
    }

// tu jest gdy Enemy strzela w twoje jednostki
    private void updateProjectiles() {
        ArrayList<Projectile> toRemove = new ArrayList<>();
        for (Projectile projectile : projectiles) {
            projectile.move();

            // Sprawdź kolizję z żołnierzami
            for (Soldier soldier : soldiers) {
                if (projectile.checkCollision(soldier)) {
                    toRemove.add(projectile);
                    soldiers.remove(soldier); // Usuń żołnierza po trafieniu
                    break;
                }
            }
            for (Artylery artylery : artylerys) {
                if (projectile.checkCollision(artylery)) {
                    toRemove.add(projectile);
                    artylerys.remove(artylery); // Usuń artea po trafieniu
                    break;
                }
            }
            for (BuilderVehicle builderVehicle : builderVehicles) {
                if (projectile.checkCollision(builderVehicle)) {
                    toRemove.add(projectile);
                    builderVehicles.remove(builderVehicle); // Usuń żołnierza po trafieniu
                    break;
                }
            }

            for (BattleVehicle battleVehicle : battleVehicles) {
                if (projectile.checkCollision(battleVehicle)) {
                    toRemove.add(projectile);
                    if (battleVehicle.takeDamage()) {
                        battleVehicles.remove(battleVehicle);
                    } // Usuń żołnierza po trafieniu
                    break;
                }

            }
            for (Harvester harvester : harvesters) {
                if (projectile.checkCollision(harvester)) {
                    toRemove.add(projectile);
                    harvesters.remove(harvester);
                    break;
                }
            }

            // Usuń pociski, które wyszły poza planszę
            if (projectile.isOutOfBounds(getWidth(), getHeight())) {
                toRemove.add(projectile);
            }
        }
        projectiles.removeAll(toRemove);
        repaint();
    }

    private void moveSoldiers() {
        for (Soldier soldier : soldiers) {
            Point target = soldier.getTarget();
            if (target != null) {
                // Oblicz przesunięcie względem celu
                int dx = target.x - soldier.getX();
                int dy = target.y - soldier.getY();
                int speed = 3;

                // Ogranicz przesunięcie do maksymalnej prędkości
                if (Math.abs(dx) > speed) dx = dx > 0 ? speed : -speed;
                if (Math.abs(dy) > speed) dy = dy > 0 ? speed : -speed;

                // Aktualizacja kierunku na podstawie przesunięcia
                soldier.updateDirection(new Point(dx, dy));


                // Przesuń żołnierza o obliczone wartości
                soldier.setPosition(soldier.getX() + dx, soldier.getY() + dy, powerPlants, baracks, soldiers, hives, battleVehicles);

                // Jeśli żołnierz osiągnął punkt docelowy, usuń cel
                if (dx == 0 && dy == 0) {
                    soldier.setTarget(null);
                }
            }
        }
        repaint();
    }


    private void moveMinigunner() {
        for (Minigunner minigunner : minigunners){
            Point target = minigunner.getTarget();
            if (target != null) {
                int dx = target.x - minigunner.getX();
                int dy = target.y - minigunner.getY();
                int speed = 3;
                if (Math.abs(dx) > speed) dx = dx > 0 ? speed : -speed;
                if (Math.abs(dy) > speed) dy = dy > 0 ? speed : -speed;

                minigunner.setPosition(minigunner.getX() + dx, minigunner.getY() + dy, powerPlants, baracks, soldiers, hives, battleVehicles);

                if (dx == 0 && dy == 0) {
                    minigunner.setTarget(null);

                }
            }
        }
    }

    private void moveBattleVehicle() {
        for (BattleVehicle battleVehicle : battleVehicles) {
            Point target = battleVehicle.getTarget();
            if (target != null) {
                // Oblicz środek pojazdu
                int centerX = battleVehicle.getX() + battleVehicle.getBounds().width / 2;
                int centerY = battleVehicle.getY() + battleVehicle.getBounds().height / 2;
                // Oblicz różnicę między środkiem a celem
                int dx = target.x - centerX;
                int dy = target.y - centerY;
                int speed = 3;
                if (Math.abs(dx) > speed) {
                    dx = dx > 0 ? speed : -speed;
                }
                if (Math.abs(dy) > speed) {
                    dy = dy > 0 ? speed : -speed;
                }
                // Przesuń pojazd – top-left zmienia się, ale środek się przesunie o (dx, dy)
                battleVehicle.setPosition(battleVehicle.getX() + dx, battleVehicle.getY() + dy, powerPlants, baracks, soldiers, hives, battleVehicles);

                // Jeśli różnica jest zerowa (środek się zrównał z celem), usuń cel
                if (dx == 0 && dy == 0) {
                    battleVehicle.setTarget(null);
                }
            }
        }
        repaint();
    }

//    private void moveBattleVehicle() {
//        for (BattleVehicle battleVehicle : battleVehicles) {
//            Point target = battleVehicle.getTarget();
//            if (target != null) {
//                int dx = target.x - battleVehicle.getX();
//                int dy = target.y - battleVehicle.getY();
//                int speed = 3;
//                if (Math.abs(dx) > speed) dx = dx > 0 ? speed : -speed;
//                if (Math.abs(dy) > speed) dy = dy > 0 ? speed : -speed;
//
//
//                battleVehicle.setPosition(battleVehicle.getX() + dx, battleVehicle.getY() + dy, powerPlants);
//
//                // Jeśli osiągnięto punkt docelowy, usuń cel
//                if (dx == 0 && dy == 0) {
//                    battleVehicle.setTarget(null);
//                }
//            }
//        }
//        repaint();
//    }

    private void moveBuilderVeicle() {
        for (BuilderVehicle builderVehicle : builderVehicles) {
            Point target = builderVehicle.getTarget();
            if (target != null) {
                int dx = target.x - builderVehicle.getX();
                int dy = target.y - builderVehicle.getY();
                int speed = 2;
                if (Math.abs(dx) > speed) dx = dx > 0 ? speed : -speed;
                if (Math.abs(dy) > speed) dy = dy > 0 ? speed : -speed;

                builderVehicle.setPosition(builderVehicle.getX() + dx, builderVehicle.getY() + dy);

                if (dx == 0 && dy == 0) {
                    builderVehicle.setTarget(null);
                }
            }
        }
        repaint();
    }
    private void moveArtylery() {
        for (Artylery artylery : artylerys) {
            Point target = artylery.getTarget();
            if (target != null) {
                int dx = target.x - artylery.getX();
                int dy = target.y - artylery.getY();
                int speed = 2;
                if (Math.abs(dx) > speed) dx = dx > 0 ? speed : -speed;
                if (Math.abs(dy) > speed) dy = dy > 0 ? speed : -speed;

                artylery.setPosition(artylery.getX() + dx, artylery.getY() + dy);

                if (dx == 0 && dy == 0) {
                    artylery.setTarget(null);
                }
            }
        }
        repaint();
    }

    private void moveHarvesters() {
        for (Harvester harvester : harvesters) {
            Point target = harvester.getTarget();
            if (target != null) {
                int dx = target.x - harvester.getX();
                int dy = target.y - harvester.getY();
                int speed = 3;
                if (Math.abs(dx) > speed) dx = dx > 0 ? speed : -speed;
                if (Math.abs(dy) > speed) dy = dy > 0 ? speed : -speed;

                harvester.setPosition(harvester.getX() + dx, harvester.getY() + dy);

                if (dx == 0 && dy == 0) {
                    harvester.setTarget(null);
                }

            }

        }
        repaint();
    }


    private void shootEnemiesart() {
        ArrayList<ArtBullet> artBulletsToRemove = new ArrayList<>();

        for (ArtBullet artBullet : artBullets) {
            artBullet.move();

            // Jeśli pocisk dotarł do celu, twórz eksplozję
            if (artBullet.hasReachedTarget()) {
                createExplosion((int) artBullet.getX(), (int) artBullet.getY()); // Tworzymy eksplozję w miejscu pocisku
                artBulletsToRemove.add(artBullet); // Dodajemy pocisk do usunięcia, ponieważ osiągnął cel
            }
        }

        // Usuwamy pociski, które dotarły do celu
        artBullets.removeAll(artBulletsToRemove);
    }

    private void minigunnerBulletshoot() { // to do strzelania w wrogow
        ArrayList<MinigunnerBullet> minigunnerBulletToRemove = new ArrayList<>();

        for (MinigunnerBullet minigunnerBullet : minigunnerBullets) {
            minigunnerBullet.move();
            if (minigunnerBullet.isOutOfBounds(getWidth(), getHeight())) {
                minigunnerBulletToRemove.add(minigunnerBullet);

            }
            else {
                for (Enemy enemy : enemies) {
                    if (minigunnerBullet.checkCollision(enemy)) {
                        minigunnerBulletToRemove.add(minigunnerBullet);
                        if (enemy.takeDamage()){
                            enemies.remove(enemy);
                        }
                        break;
                    }
                }
//                boolean hit = false;
                for (Hive hive : hives) {
                    if (minigunnerBullet.checkCollision(hive)) {
                        minigunnerBulletToRemove.add(minigunnerBullet);
//                        hit = true;
                        if (hive.takeDamage()) {
                            hives.remove(hive);
                        }
                        break;
                    }
                }

                for (EnemyShooter enemyShooter : enemyShooters) {
                    if (minigunnerBullet.checkCollision(enemyShooter)) {
                        minigunnerBulletToRemove.add(minigunnerBullet);
//                        hit = true;
                        if (enemyShooter.takeDamage()) {
                            enemyShooters.remove(enemyShooter);
                        }
                        break;
                    }
                }
                for (EnemyHunter enemyHunter : enemyHunters) {
                    if (minigunnerBullet.checkCollision(enemyHunter)) {
                        minigunnerBulletToRemove.add(minigunnerBullet);
//                        hit = true;
                        if (enemyHunter.takeDamage()) {
                            enemyHunters.remove(enemyHunter);
                        }
                        break;
                    }
                }

                // Sprawdzanie kolizji z EnemyToo
                for (EnemyToo enemyToo : enemiesToo) {
                    if (minigunnerBullet.checkCollision(enemyToo)) {
                        minigunnerBulletToRemove.add(minigunnerBullet);
                        if (enemyToo.takeDamage()){
                            enemiesToo.remove(enemyToo);
                        }
                        break;
                    }
                }
            }
        }
        minigunnerBullets.removeAll(minigunnerBulletToRemove);

        repaint();
    }
//////////////////////////////////////////////////////////////////////////////////////////
    private void shootEnemies() { // to do strzelania w wrogow za pomoca bullet
        ArrayList<Bullet> bulletsToRemove = new ArrayList<>();

        for (Bullet bullet : bullets) {
            bullet.move();
            if (bullet.isOutOfBounds(getWidth(), getHeight())) {
                bulletsToRemove.add(bullet);

            }
            else {
                for (Enemy enemy : enemies) {
                    if (bullet.checkCollision(enemy)) {
                        bulletsToRemove.add(bullet);
                        if (enemy.takeDamage2()){
                        enemies.remove(enemy);
                        }
                        break;
                    }
                }
//                boolean hit = false;
                for (Hive hive : hives) {
                    if (bullet.checkCollision(hive)) {
                        bulletsToRemove.add(bullet);
//                        hit = true;
                        if (hive.takeDamage()) {
                            hives.remove(hive);
                        }
                        break;
                    }

                }
                for (EnemyShooter enemyShooter : enemyShooters) {
                    if (bullet.checkCollision(enemyShooter)) {
                        bulletsToRemove.add(bullet);
//                        hit = true;
                        if (enemyShooter.takeDamage()) {
                            enemyShooters.remove(enemyShooter);
                        }
                        break;
                    }
                }
                for (EnemyHunter enemyHunter : enemyHunters) {
                    if (bullet.checkCollision(enemyHunter)) {
                        bulletsToRemove.add(bullet);
//                        hit = true;
                        if (enemyHunter.takeDamage()) {
                            enemyHunters.remove(enemyHunter);
                        }
                        break;
                    }
                }

                // Sprawdzanie kolizji z EnemyToo
                for (EnemyToo enemyToo : enemiesToo) {
                    if (bullet.checkCollision(enemyToo)) {
                        bulletsToRemove.add(bullet);
                        if (enemyToo.takeDamage2()){
                        enemiesToo.remove(enemyToo);
                        }
                        break;
                    }


                }
            }
        }
        bullets.removeAll(bulletsToRemove);

        repaint();
    }
//update co się dzieje w grze
    private void updateGame() {

        bullets.removeIf(bullet -> bullet.isOutOfBounds(getWidth(), getHeight()) || bullet.isExpired());
        projectiles.removeIf(projectile -> projectile.isOutOfBounds(getWidth(), getHeight()) || projectile.isExpired());
        minigunnerBullets.removeIf(minigunnerBullet -> minigunnerBullet.isOutOfBounds(getWidth(), getHeight()) || minigunnerBullet.isExpired());



        for (EnemyToo enemyToo : enemiesToo) {
            enemyToo.update(soldiers, harvesters, baracks, builderVehicles, artylerys, battleVehicles, powerPlants, factories); // Przekazuje listę żołnierzy do śledzenia
        }


        for (EnemyShooter enemyShooter : enemyShooters) {
            enemyShooter.update(soldiers, harvesters, builderVehicles, artylerys, battleVehicles, powerPlants, factories);
        }
        for (Enemy enemy : enemies) {
            enemy.move();
        }
        for (Harvester harvester : harvesters) { // Iteracja po liście harvesterów
            for (ResourcesSteel resource : resources) { // Iteracja po zasobach
                if (!resource.isDepleted() && harvester.getBounds().intersects(resource.getBounds())) {
                    // Harvester wydobywa zasoby
                    resource.mineResource(1); // Zmniejsz ilość zasobów o 3 na sekundę
                    collectedSteel += 1;     // Zwiększ liczbę zebranych zasobów
                }
            }
        }
        repaint();
    }


/////////////////////////// to jest ten, no, spawn wrogow co jakis czas na mapie losowo
    @Override
    public void actionPerformed(ActionEvent e) {
        long currentTime = System.currentTimeMillis();

        // Sprawdzanie czy minęło 45 sekund
        if (currentTime - lastSpawnTime >= SPAWN_INTERVAL) {
            spawnEnemyToo();
            lastSpawnTime = currentTime;
        }

        repaint();
    }


    private void spawnEnemyToo() {
        Random rand = new Random();

        // Obliczanie liczby wrogów, zwiększanej o 20% z każdą falą
        int numEnemies = (int) Math.ceil(baseEnemyCount * Math.pow(1.20, waveNumber - 1));

        for (int i = 0; i < numEnemies; i++) {
            int x = 1620 + rand.nextInt(111); // Pozycja na prawej krawędzi poza mapą (poza szerokością 1600)
            int y = rand.nextInt(900); // Losowa pozycja na osi Y w granicach wysokości mapy (0-900)
            enemiesToo.add(new EnemyToo(x, y)); // Dodajemy nowego wroga do listy enemiesToo
        }

        waveNumber++; // Przechodzimy do następnej fali
    }

//    private void spawnEnemyToo() {
//        Random rand = new Random();
//       // int numEnemies = random.nextInt(11) + 10; // Losowa liczba między 10 a 20
//        for (int i = 0; i < 18; i++) { // Liczba przeciwników do utworzenia
//
//
//                int x = 1620 + rand.nextInt(111); // Pozycja na prawej krawędzi poza mapą (poza szerokością 1600)
//               int y = rand.nextInt(900); // Losowa pozycja na osi Y w granicach wysokości mapy (0-900)
//
//
//
////        for (int i = 0; i < 15; i++) {
////            int side = rand.nextInt(4);  // Losujemy, z której strony pojawi się przeciwnik
////            int x = 0, y = 0;
//
//
////            if (side == 0) {  // Górna część
////                x = rand.nextInt(1700);  // Losowa pozycja na osi X w granicach szerokości planszy
////                y = -20;  // Pozycja na górze poza mapą
////            }
////            // tu poniżej musi być else if/////////////////////
////            if (side == 1) {  // Dolna część
////                x = rand.nextInt(1700);  // Losowa pozycja na osi X w granicach szerokości planszy
////                y = 520;  // Pozycja na dole poza mapą
////            }
////            else if (side == 2) {  // Lewa część
////                x = -20;  // Pozycja na lewej krawędzi poza mapą
////                y = rand.nextInt(800);  // Losowa pozycja na osi Y w granicach wysokości planszy
////            }
////             if (side == 3) {  // Prawa część
////                x = 1420;  // Pozycja na prawej krawędzi poza mapą
////                y = rand.nextInt(800);  // Losowa pozycja na osi Y w granicach wysokości planszy
////            }
//
////            enemiesToo.add(new EnemyToo(rand.nextInt(1700), rand.nextInt(750)));
//            enemiesToo.add(new EnemyToo(x, y));
//
//        }
//        }
//
//


    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_S) { // Klawisz 'S'
            stopAllSoldiers(); // Wywołaj metodę zatrzymania żołnierzy
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {
        // Niepotrzebne tutaj
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Niepotrzebne tutaj
    }

    @Override
    public void mousePressed(MouseEvent e) {

        if (SwingUtilities.isLeftMouseButton(e)) {
            startPoint = e.getPoint(); // Zapisanie punktu początkowego zaznaczenia
            selectionRectangle = new Rectangle(); // Inicjalizacja prostokąta zaznaczenia


            // Zaznacz żołnierza
            for (Minigunner minigunner : minigunners) {
                minigunner.setSelected(false);
                if (new Rectangle(minigunner.getX(), minigunner.getY(), 20, 20).contains(e.getPoint())) {
                    selectedMinigunner = minigunner;
                    minigunner.setSelected(true);
                    return;
                }
                else  {
                    minigunner.setSelected(false);
                }
            }

            for (Soldier soldier : soldiers) {
                soldier.setSelected(false);

                if (new Rectangle(soldier.getX(), soldier.getY(), 20, 20).contains(e.getPoint())) {
                    selectedSoldier = soldier;
                    soldier.setSelected(true);
                    return;
                }
//                    else if (selectedSoldier != null){
//                        selectedSoldier.setSelected(false);
//                    }
//                    repaint();
                else {
                    soldier.setSelected(false);
                }
            }
            for (BattleVehicle battleVehicle : battleVehicles) {
                battleVehicle.setSelected(false);

                if (new Rectangle(battleVehicle.getX(), battleVehicle.getY(), 40, 40).contains(e.getPoint())) {
                    selectedBattleVehicle = battleVehicle;
                    battleVehicle.setSelected(true);
                    return;
                }
                else {
                    battleVehicle.setSelected(false);
                }
            }
            // klik lewym zaznacza dziada arty
            for (Artylery artylery : artylerys) {
                artylery.setSelected(false);

                if (new Rectangle(artylery.getX(), artylery.getY(), 20,20).contains(e.getPoint())) {
                    selectedArtylery = artylery;
                    artylery.setSelected(true);
                    return;
                }
                else  {
                    artylery.setSelected(false);
                }
            }

// gdy klikniedsz lewym na herv, zostaje wybarny
            for (Harvester harvester : harvesters) {
                harvester.setSelected(false);

                if (new Rectangle(harvester.getX(), harvester.getY(), 30, 30).contains(e.getPoint())) {
                    selectedHarvester = harvester;
                    harvester.setSelected(true);
                    return;

                } else {
                    harvester.setSelected(false);
                }
            }
            for (BuilderVehicle builderVehicle : builderVehicles){
                builderVehicle.setSelected(false);

                if (new Rectangle(builderVehicle.getX(), builderVehicle.getY(),25, 25).contains(e.getPoint())){
                    selectedBuilderVehicle = builderVehicle;
                    builderVehicle.setSelected(true);
                    System.out.println("Zaznaczono BuilderVehicle.");
                    showBuilderMenu = true; // Pokaż menu budowy
                    updateBuilderMenu();
                    return;
                } else {
                    builderVehicle.setSelected(false);
                }
            }
            for (Factory factory : factories){
                factory.setSelected(false);

                if (new Rectangle(factory.getX(), factory.getY(),55, 55).contains(e.getPoint())){
                    selectedFactories = factory;
                    factory.setSelected(true);
                    System.out.println("Zaznaczono Factory.");
                    showFactorysMenu = true; // Pokaż menu produkcji factory
                    updateFactorysMenu();
                    return;
                } else {
                    factory.setSelected(false);
                }
            }
            for (Baracks baracks : baracks){
                baracks.setSelected(false);

                if (new Rectangle(baracks.getX(), baracks.getY(), 80, 80 ).contains(e.getPoint())){
                    selectedBaracks = baracks;
                    baracks.setSelected(true);
                    System.out.println("Zaznaczono baracks");
                    showBaracksMenu = true; // pokaz menu produkcji
                    updateBaracksMenu();
                    return;
                } else {
                    baracks.setSelected(false);
                }
            }
            boolean unitSelected = false;
            for (Soldier soldier : soldiers) {
                if (new Rectangle(soldier.getX(), soldier.getY(), 20, 20).contains(e.getPoint())) {
                    selectedSoldier = soldier;
                    soldier.setSelected(true);
                    unitSelected = true;
                    break;
                }
            }
            // Jeśli żadna jednostka nie została zaznaczona, pozwól na przeciąganie
            if (!unitSelected) {
                startPoint = e.getPoint();
                selectionRectangle = new Rectangle();
            }
        }


//        int mouseX = e.getX();
//        int mouseY = e.getY();
//
//        // Sprawdź, czy kliknięto na koszary
//        for (Baracks b : baracks) {
//            if (b.contains(mouseX, mouseY)) {
//                // Zaznacz koszary
//                for (Baracks other : baracks) {
//                    other.setSelected(false); // Odznacz pozostałe
//                }
//                b.setSelected(true);
//                showBaracksMenu = true; // Wyświetl menu koszar
//                updateBaracksMenu();
//                repaint();
//                return;
//            }
//        }
//
//        // Jeśli kliknięto poza koszarami, odznacz wszystkie
//        for (Baracks b : baracks) {
//            b.setSelected(false);
//        }
//        showBaracksMenu = false;
//        updateBaracksMenu();


        else if (SwingUtilities.isRightMouseButton(e)) {
            // Odznacz żołnierza
            if (selectedSoldier != null) {
                selectedSoldier.setSelected(false);
                selectedSoldier = null;
            }
            if (selectedMinigunner != null) {
                selectedMinigunner.setSelected(false);
                selectedMinigunner = null;
            }
            if (selectedArtylery != null) {
                selectedArtylery.setSelected(false);
                selectedArtylery = null;
            }


            if (selectedBattleVehicle != null) {
                selectedBattleVehicle.setSelected(false);
                selectedBattleVehicle = null;
            }
            // kliknieciem prawym odznaczy zaznaczone jednostki
             if (selectedHarvester != null) {
                selectedHarvester.setSelected(false);
                selectedHarvester = null;
            }

             if (selectedBuilderVehicle != null){
                 selectedBuilderVehicle.setSelected(false);
                 showBuilderMenu = false;
                 updateBuilderMenu();
                 selectedBuilderVehicle = null;
             }
             if (selectedBaracks != null) {
                 selectedBaracks.setSelected(false);
                 showBaracksMenu = false;
                 updateBaracksMenu();
                 selectedBaracks = null;
             }
            if (selectedFactories != null) {
                selectedFactories.setSelected(false);
                showFactorysMenu = false;
                updateFactorysMenu();
                selectedFactories = null;
            }
            for (Soldier soldier : soldiers) {
                soldier.setSelected(false);
            }

        }
        if (selectedHarvester != null && SwingUtilities.isLeftMouseButton(e)){
            selectedHarvester.setTarget(e.getPoint());
            selectedHarvester.setSelected(true);

        }
        if (selectedBattleVehicle != null && SwingUtilities.isLeftMouseButton(e)){
            selectedBattleVehicle.setTarget(e.getPoint());
            selectedBattleVehicle.setSelected(true);
        }

        if (selectedBuilderVehicle != null && SwingUtilities.isLeftMouseButton(e)) {
            selectedBuilderVehicle.setTarget(e.getPoint());
            selectedBuilderVehicle.setSelected(true);

        }
        if (selectedArtylery != null && SwingUtilities.isLeftMouseButton(e)){
            selectedArtylery.setTarget(e.getPoint());
            selectedArtylery.setSelected(true);
        }

        // Jeśli wybrano żołnierza, ustaw cel
        if (selectedSoldier != null && SwingUtilities.isLeftMouseButton(e)) {
            selectedSoldier.setTarget(e.getPoint());
            selectedSoldier.setSelected(true);
        }
        if (selectedMinigunner != null && SwingUtilities.isLeftMouseButton(e))
        {
            selectedMinigunner.setTarget(e.getPoint());
            selectedMinigunner.setSelected(true);
        }

        repaint();
    }
    @Override
    public void mouseDragged(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && startPoint != null) {
            // Aktualizowanie prostokąta zaznaczenia podczas przeciągania
            int x = Math.min(startPoint.x, e.getX());
            int y = Math.min(startPoint.y, e.getY());
            int width = Math.abs(startPoint.x - e.getX());
            int height = Math.abs(startPoint.y - e.getY());
            selectionRectangle = new Rectangle(x, y, width, height);

            repaint();
        }
    }

////////////// zazanczanie wielu jednostek - na razie ttylko soldier
    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && selectionRectangle != null) {
            // Zaznacz jednostki w prostokącie
            for (Soldier soldier : soldiers) {
                if (selectionRectangle.intersects(new Rectangle(soldier.getX(), soldier.getY(), 20, 20))) {
                    soldier.setSelected(true);
                    selectedSoldiers.add(soldier); // Dodaj żołnierza do listy zaznaczonych
                }
            }

            // Zakończ rysowanie prostokąta zaznaczenia
            selectionRectangle = null;
            startPoint = null;
        } else if (SwingUtilities.isRightMouseButton(e)) {
            // Odznacz wszystkie zaznaczone jednostki
            for (Soldier soldier : selectedSoldiers) {
                soldier.setSelected(false);
            }
            selectedSoldiers.clear(); // Wyczyszczenie listy zaznaczonych żołnierzy
        }

        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            // Jeśli zaznaczono jakichś żołnierzy, ustaw cel ruchu dla wszystkich
            if (!selectedSoldiers.isEmpty()) {
                Point target = e.getPoint();
                for (Soldier soldier : selectedSoldiers) {
                    soldier.setTarget(target); // Ustaw cel dla każdego zaznaczonego żołnierza
                    soldier.setSelected(true); // Zachowaj zaznaczenie dla każdego żołnierza
                }
            }
        }

        repaint();
    }


//    // Obsługuje kliknięcia myszy z terraBionX1
//    private void handleMousePress(MouseEvent e) {
//        if (e.getButton() == MouseEvent.BUTTON1) { // Lewy przycisk myszy
//            int mouseX = e.getX();
//            int mouseY = e.getY();
//
//            // Sprawdzamy, czy kliknięto na żołnierza
//            for (Soldier soldier : soldiers) {
//                if (soldier.contains(mouseX, mouseY)) {
//                    selectedSoldier = soldier;
//                    return;
//                }
//            }
//
//            // Jeśli jest już wybrany żołnierz, ustaw cel ruchu
//            if (selectedSoldier != null) {
//                targetPoint = new Point(mouseX, mouseY);
//            }
//        }
//    }

//    @Override
//    public void mouseReleased(MouseEvent e) {}

//    @Override
//    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {}

//    @Override
//    public void mouseDragged(MouseEvent e) {}

//    @Override
//    public void actionPerformed(ActionEvent e) {
//        // Logika reakcji na zdarzenia
//    }

@Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    // Rysowanie mapy (w tym przypadku tylko prostokąt)

    g.fillRect(0, 0, 3000, 3000); // Rysowanie mapy 3000x3000

    long currentTime = System.currentTimeMillis();
    long deltaTime = currentTime - previousTime;
    previousTime = currentTime;

//        setBackground(Color.WHITE);
    // Rysowanie tła
    if (backgroundImage != null) {
        int imgWidth = backgroundImage.getWidth(null);
        int imgHeight = backgroundImage.getHeight(null);

        for (int x = 0; x < getWidth(); x += imgWidth) {
            for (int y = 0; y < getHeight(); y += imgHeight) {
                g.drawImage(backgroundImage, x, y, this);
            }
        }
    }
    for (ResourcesSteel resource : resources) {
        resource.draw(g);

        // Ustawianie większej czcionki
        Font largeFont = new Font("Arial", Font.BOLD, 15); // Czcionka Arial, pogrubiona, rozmiar 15
        g.setFont(largeFont);

        g.setColor(Color.WHITE);
        g.drawString("Steel Collected: " + collectedSteel, getWidth() - 300, 20); // Zwiększono przesunięcie X, aby tekst się zmieścił
        g.drawString("Power: " + totalPower, getWidth() - 200, 50); // Przesunięcie Y zwiększone, by uniknąć nachodzenia tekstu
    }
    for (Baracks b : baracks) {
        b.draw(g);
    }

    for (int i = explosions.size() - 1; i >= 0; i--) {
        Explosion explosion = explosions.get(i);
        explosion.draw(g);
        explosion.checkEnemyCollision(enemiesToo, enemies, hives, enemyHunters, enemyShooters); // Sprawdza kolizję z wrogami

        if (explosion.isExpired()) {
            explosions.remove(i); // Usunięcie eksplozji, gdy animacja się kończy
        }
    }

    if (selectionRectangle != null) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(0, 0, 255, 50)); // Półprzezroczysty niebieski
        g2d.fill(selectionRectangle);
        g2d.setColor(Color.BLUE); // Krawędź prostokąta
        g2d.draw(selectionRectangle);
    }

    // Rysowanie elektrowni
    for (PowerPlant powerPlant : powerPlants) {
        powerPlant.draw(g);
    }
    for (Factory factory : factories) {
        factory.draw(g);
    }

    for (Harvester harvester : harvesters) {
        harvester.draw(g);
    }
    for (Cryopit cryopit : cryopits){
        cryopit.drawAll(g);
    }
    // Rysowanie żołnierzy
    for (Soldier soldier : soldiers) {
        soldier.draw(g);
        soldier.shoot(g, bullets, enemies, enemiesToo, hives, enemyShooters, enemyHunters); // Żołnierz strzela
    }
    for (Minigunner minigunner : minigunners) {
        minigunner.draw(g);
        minigunner.shoot(g, minigunnerBullets, enemies, enemiesToo, hives, enemyShooters, enemyHunters); // Żołnierz strzela
    }

    for (BattleVehicle battleVehicle : battleVehicles){
        battleVehicle.draw(g);
        battleVehicle.update(deltaTime);
        battleVehicle.shoot(g, bullets, enemies, enemiesToo, hives, enemyShooters);
    }
    for (Artylery artylery : artylerys){
        artylery.draw(g);
        artylery.shoot(g, artBullets, enemies, enemiesToo, hives, enemyShooters);
    }

    for (Hive hive : hives) {
        hive.draw(g);
        hive.spawnEnemiesToo(g, enemiesToo, enemyShooters, enemyHunters); // Aktualizuje Hive i spawnuje EnemyToo
    }

        for (EnemyHunter enemyHunter : enemyHunters){
            enemyHunter.update(soldiers, harvesters, builderVehicles, artylerys, battleVehicles, powerPlants, factories, enemyHunters);
            enemyHunter.draw(g);
        }


    // Rysowanie wrogów
    for (Enemy enemy : enemies) {
        enemy.draw(g);
    }
    for (EnemyShooter enemyShooter : enemyShooters){
        enemyShooter.draw(g);
        enemyShooter.shoot(g, projectiles, soldiers, battleVehicles);
    }
    //budowniczy
    for (BuilderVehicle builderVehicle :builderVehicles) {
        builderVehicle.draw(g);
    }


    // Rysowanie wrogów EnemyToo
    for (EnemyToo enemyToo : enemiesToo) {
        enemyToo.draw(g);
    }


    // Rysowanie pocisków
    for (Bullet bullet : bullets) {
        bullet.draw(g);
    }
    for (MinigunnerBullet minigunnerBullet : minigunnerBullets){
        minigunnerBullet.draw(g);
    }
    for (ArtBullet artBullet : artBullets) {
        artBullet.draw(g);
    }

    for (Hive hive : hives) {
        hive.draw(g); // Rysowanie Hive
    }

    // Rysowanie pocisków
    for (Projectile projectile : projectiles) {
        projectile.draw(g);
    }

    for (Factory factory : factories) {
        factory.draw(g);
    }



    // Rysowanie prostokąta zaznaczenia
    if (selectionRectangle != null) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(0, 0, 255, 50)); // Półprzezroczysty niebieski
        g2d.fill(selectionRectangle);
        g2d.setColor(Color.BLUE); // Krawędź prostokąta
        g2d.draw(selectionRectangle);
    }



}
}