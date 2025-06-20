
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
    private ArrayList<SteelMine> steelMines;
    private Soldier selectedSoldier;
    private Minigunner selectedMinigunner;
    private BattleVehicle selectedBattleVehicle;
    private Artylery selectedArtylery;
    private Harvester selectedHarvester;
    private BuilderVehicle selectedBuilderVehicle;
    private Baracks selectedBaracks;
    private SteelMine selectedSteelMines;
    private Factory selectedFactories;

    private JLabel countdownLabel; // to jest do tego by odliczalo budowe pojazdow

    private Soldier soldier; // to jest do zapisywania do calego Soldier w savegame i load

    private Timer movementTimer;
    private Timer shootingTimer;
    private Timer shootingTimer2;
    private Timer enemyShootingTimer;
    private ArrayList<Projectile> projectiles;
    private ArrayList<ResourcesSteel> resources;
    private ArrayList<PowerPlant> powerPlants;
    private int collectedSteel = 10000; // Przechowuje zebraną ilość stali
    private int totalPower = 0;
    private final int MAX_POWER = 200;


    private JButton btnPowerPlant;
    private JButton btnSteelMine;
    private JButton btnBaracks;
    private JButton btnFactory;

    private JButton btnSoldier;

    private JButton btnHarvester;
    private JButton btnBattleVehicle;
    private JButton btnArtylery;
    private JButton btnBuilderVehicle;


    // to do wskazywania miejsca gdzie budowac np powerplant itp na przyszlosc
    private Rectangle placementCursor;

    //do przesowania myszka
    private final int SCROLL_EDGE_SIZE = 20; // ile pikseli od krawędzi reaguje
    private final int SCROLL_SPEED = 20;     // ile pikseli przewija co tick
    private Timer scrollTimer;
    private Point mousePosition;
    private JFrame mainFrame;

    private ArrayList<Hive> hives;
    private BufferedImage backgroundImage;

    private long lastSpawnTime = System.currentTimeMillis();
    private static final long SPAWN_INTERVAL = 45000; // resoawn przeciwnika w milisekundach - pojawiaja sie od prawej strony mapy
    private Timer timer;
    private long previousTime = System.currentTimeMillis();



    private boolean showBuilderMenu = false;
    private boolean showBaracksMenu = false;
    private boolean showFactorysMenu = false;


    //do budowania elektrowni
    private enum BuildingType {
        POWER_PLANT, STEEL_MINE, BARRACKS, FACTORY
    }

    private boolean isPlacingBuilding = false;
    private BuildingType buildingToPlace = null;

    private boolean isPlacingPowerPlant = false;
    private boolean isPlacingFactory = false;
    private boolean isPlacingSteelMine = false;
    private boolean isPlacingBarracks = false;
    private Point currentMousePosition = null;
    private final int BUILD_RANGE = 170;
    private static final int BUILD_SIZE = 80;

    private JScrollPane scrollPane;

    public void setScrollPane(JScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }

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

    public int getCollectedSteel() {
        return collectedSteel;
    }

    public int getTotalPower() {
        return totalPower;
    }

    private HUDPanel hudPanel; // dodaj pole

    public void setHUDPanel(HUDPanel hudPanel) {
        this.hudPanel = hudPanel;
    }

    // gdy zmieniasz zasoby:
    public void addSteel(int amount) {
        collectedSteel += amount;
        if (hudPanel != null) {
            hudPanel.repaint();
        }
    }


    public GamePanel(JFrame frame) {

        // to do przesuwania myszka po mapie - by dal osie zamiast strzalkami przesowac mapa
        this.mainFrame = frame;

        // Timer sprawdzający pozycję myszy
        scrollTimer = new Timer(20, e -> checkEdgeScrolling());
        scrollTimer.start();

        // Aktualizuj pozycję myszy
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mousePosition = e.getPoint();

                if (isPlacingBuilding && selectedBuilderVehicle != null) {
                    int x = e.getX();
                    int y = e.getY();

                    // Płynna pozycja — bez wyrównania do siatki
                    placementCursor = new Rectangle(x, y, BUILD_SIZE, BUILD_SIZE);
                    repaint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mousePosition = e.getPoint();
            }
        });




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
        this.steelMines = new ArrayList<>();
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
        this.selectedSteelMines = null;
        this.selectedFactories = null;
        this.explosions = new ArrayList<>();

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.projectiles = new ArrayList<>();
        this.baracks = new ArrayList<>();
        this.factories = new ArrayList<>();
        this.steelMines = new ArrayList<>();
        this.buldings = new ArrayList<>();


        timer = new Timer(1000 / 60, this); // Wywołanie actionPerformed co ~16 ms
        timer.start();

        Timer productionUpdateTimer = new Timer(1000, e -> {
            for (Factory factory : factories) {
                factory.updateProduction();
            }
            repaint();
        });
        productionUpdateTimer.start();


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
        btnHarvester.setBounds(10, 90, 120, 30); // Pozycja i rozmiar
        btnHarvester.setVisible(false);
        add(btnHarvester);

        btnArtylery = new JButton("Artylery");
        btnArtylery.setBounds(10, 130, 120, 30);
        btnArtylery.setVisible(false);
        add(btnArtylery);


        btnBattleVehicle = new JButton("Armored Vehicle");
        btnBattleVehicle.setBounds(10, 170, 120, 30); // Pozycja i rozmiar
        btnBattleVehicle.setVisible(false);
        add(btnBattleVehicle);


        btnBuilderVehicle = new JButton( "FENIX Drone");
        btnBuilderVehicle.setBounds(10, 210, 120, 30);
        btnBuilderVehicle.setVisible(false);
        add(btnBuilderVehicle);

        // to sluzy do odliczania czasu budowy i wyswietlania
        countdownLabel = new JLabel("");
        countdownLabel.setBounds(140, 130, 60, 30); // Po prawej stronie przycisku
        countdownLabel.setVisible(false);
        add(countdownLabel);


        btnHarvester.addActionListener(e -> {
            if (selectedFactories != null) {
                if (collectedSteel >= 2000) {
                    // Oblicz pozycję jednostki obok Baracks
                    int harvesterX = selectedFactories.getX() + 120;
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
                    int artyleryX = selectedFactories.getX() + 120;
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
        btnBuilderVehicle.addActionListener(e -> {
            if (selectedFactories != null) {
                if (collectedSteel >= 2000) {
                    if (builderVehicles.size() >= 5) {
                        System.out.println("Limit FENIX Drone osiągnięty (max 5).");
                        return; // Nie buduj więcej
                    }

                    int builderVehicleX = selectedFactories.getX() + 120;
                    int builderVehicleY = selectedFactories.getY();

                    collectedSteel -= 2000;

                    selectedFactories.startProduction(10); // nowa metoda fabryki
                    updateFactorysMenu();
                    repaint();

                    Timer countdownTimer = new Timer(1000, new ActionListener() {
                        int secondsLeft = 10;

                        @Override
                        public void actionPerformed(ActionEvent event) {
                            secondsLeft--;
                            if (secondsLeft <= 0) {
                                ((Timer) event.getSource()).stop();

                                // Sprawdzenie ponownie (jeśli coś zmieniło się przez te 10 sekund)
                                if (builderVehicles.size() < 5) {
                                    builderVehicles.add(new BuilderVehicle(builderVehicleX, builderVehicleY));
                                    System.out.println("Dodano Armored Vehicle po 10 sekundach.");
                                } else {
                                    System.out.println("Produkcja anulowana - osiągnięto limit 5 FENIX.");
                                }

                                showFactorysMenu = false;
                                updateFactorysMenu();
                                repaint();
                            }
                        }
                    });
                    countdownTimer.start();
                }
            }
        });
        setLayout(null);

        btnBattleVehicle.addActionListener(e -> {
            if (selectedFactories != null) {
                if (collectedSteel >= 2000) {
                    int battleVehicleX = selectedFactories.getX() + 120;
                    int battleVehicleY = selectedFactories.getY();

                    collectedSteel -= 2000;
                    System.out.println("Produkcja pojazdu opancerzonego... (10 sekund)");
                    selectedFactories.startProduction(10); // nowa metoda fabryki
                    updateFactorysMenu();
                    repaint();

                    Timer countdownTimer = new Timer(1000, new ActionListener() {
                        int secondsLeft = 10;

                        @Override
                        public void actionPerformed(ActionEvent event) {
                            secondsLeft--;
                            if (secondsLeft <= 0) {
                                ((Timer) event.getSource()).stop();
                                battleVehicles.add(new BattleVehicle(battleVehicleX, battleVehicleY));
                                System.out.println("Dodano Armored Vehicle po 10 sekundach.");
                                showFactorysMenu = false;
                                updateFactorysMenu();
                                repaint();
                            }
                        }
                    });
                    countdownTimer.start();

                } else {
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
        setLayout(null); // Ustawienie ręcznego układu dla dodania przycisków
        // Tworzenie przycisków
        btnPowerPlant = new JButton("Power Plant");
        btnPowerPlant.setBounds(10, 90, 120, 30);
        btnPowerPlant.setVisible(false);
        add(btnPowerPlant);

        btnSteelMine = new JButton("Steel Mine");
        btnSteelMine.setBounds(10,130,120,30);
        btnSteelMine.setVisible(false);
        btnSteelMine.setEnabled(false);
        add(btnSteelMine);

        btnBaracks = new JButton("Barracks");
        btnBaracks.setBounds(10, 170, 120, 30);
        btnBaracks.setVisible(false);    // Ukryj na starcie
        btnBaracks.setEnabled(false);    // Zablokuj kliknięcie na starcie
        add(btnBaracks);

        btnFactory = new JButton("Factory");
        btnFactory.setBounds(10, 210, 120, 30);
        btnFactory.setVisible(false);
        btnFactory.setEnabled(false);
        add(btnFactory);

// Logika dla Power Plant

        btnPowerPlant.addActionListener(e -> {
            if (selectedBuilderVehicle != null && collectedSteel >= 1000) {
                isPlacingBuilding = true;
                buildingToPlace = BuildingType.POWER_PLANT;
                System.out.println("Wybierz miejsce budowy Power Plant.");
            }
        });

        btnSteelMine.addActionListener(e -> {
            if (selectedBuilderVehicle != null && collectedSteel >= 1500 && totalPower >= 150) {
                isPlacingBuilding = true;
                buildingToPlace = BuildingType.STEEL_MINE;
                System.out.println("Wybierz miejsce budowy Steel Mine.");
            }
        });

        btnBaracks.addActionListener(e -> {
            if (selectedBuilderVehicle != null && collectedSteel >= 1500 && totalPower >= 150) {
                isPlacingBuilding = true;
                buildingToPlace = BuildingType.BARRACKS;
                System.out.println("Wybierz miejsce budowy Baracks.");
            }
        });

        btnFactory.addActionListener(e -> {
            if (selectedBuilderVehicle != null && collectedSteel >= 3000 && totalPower >= 150) {
                isPlacingBuilding = true;
                buildingToPlace = BuildingType.FACTORY;
                System.out.println("Wybierz miejsce budowy Factory.");
            }
        });


/////////////////////////////////////////////////////////////////////////////////////////
        // Dodajemy 4 losowe pola Stalil Resources Steel
        Random rand = new Random();
        for (int i = 0; i < 13; i++) {
            int x = rand.nextInt(3000); // Losowa pozycja X na mapie
            int y = rand.nextInt(3000); // Losowa pozycja Y na mapie
            resources.add(new ResourcesSteel(x, y));
        }
        /////////////////////////////////// tu dodaje hives//// od prawej strony
        for (int i = 0; i < 11; i++) {
            int x = 920 + rand.nextInt(3000); // Losowa pozycja na mapie w zakresie od 920 pxl + 511
            int y = rand.nextInt(3000); // Losowa pozycja Y na mapie
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
        for (int i = 0; i < 8; i++) {
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


//        Timer globalCryopitSpawner = new Timer(30000, e -> spawnRandomCryopit()); // to timer co 30s pojawia sie namapie losy cryopit

// Uruchamiamy timer
//        globalCryopitSpawner.start();

// Timer dla strzelania wrogów
        enemyShootingTimer = new Timer(700, e -> enemyShoot());
        enemyShootingTimer.start();

        Timer resourcesTimer = new Timer(100, e -> {
            updateGameresources();
        });
        resourcesTimer.start();

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
//    private void spawnRandomCryopit() {
//        Random rand = new Random();
//
//        int x, y;
//        do {
//            x = rand.nextInt(1920 / 4) * 4; // Losowa pozycja w siatce 4x4
//            y = rand.nextInt(980 / 4) * 4;
//        }
//        while (!Cryopit.isPositionFree(x, y)); // Sprawdzamy, czy miejsce jest wolne
//
//        new Cryopit(x, y); // Tworzymy nowy Cryopit na tej pozycji
//        System.out.println("Nowy Cryopit pojawił się na: " + x + ", " + y);
//    }

    private void checkEdgeScrolling() {
        if (mousePosition == null || getParent() == null || !(getParent() instanceof JViewport viewport)) return;

        Point viewPos = viewport.getViewPosition();
        int newX = viewPos.x;
        int newY = viewPos.y;

        // Pobierz pozycję myszy względem okna aplikacji
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        if (pointerInfo == null) return;

        Point mouseLoc = pointerInfo.getLocation(); // pozycja myszy na ekranie
        Point frameLoc = mainFrame.getLocationOnScreen(); // pozycja okna gry na ekranie

        int mouseX = mouseLoc.x - frameLoc.x;
        int mouseY = mouseLoc.y - frameLoc.y;
        int frameWidth = mainFrame.getWidth();
        int frameHeight = mainFrame.getHeight();

        // Teraz sprawdzamy odległość od krawędzi okna gry
        if (mouseX <= SCROLL_EDGE_SIZE) {
            newX -= SCROLL_SPEED;
        } else if (mouseX >= frameWidth - SCROLL_EDGE_SIZE) {
            newX += SCROLL_SPEED;
        }

        if (mouseY <= SCROLL_EDGE_SIZE) {
            newY -= SCROLL_SPEED;
        } else if (mouseY >= frameHeight - SCROLL_EDGE_SIZE) {
            newY += SCROLL_SPEED;
        }

        // Ograniczenie, żeby nie wyjść poza planszę
        newX = Math.max(0, Math.min(newX, getWidth() - viewport.getWidth()));
        newY = Math.max(0, Math.min(newY, getHeight() - viewport.getHeight()));

        viewport.setViewPosition(new Point(newX, newY));
    }


    public void createExplosion(int x, int y) {
        explosions.add(new Explosion(x, y)); // Dodaje nową eksplozję do listy
    }




    // menu buldiera
    private void updateBuilderMenu() {
        btnPowerPlant.setVisible(showBuilderMenu);
        btnSteelMine.setVisible(showBuilderMenu);
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
        btnBuilderVehicle.setVisible((showFactorysMenu));
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
            for (PowerPlant powerPlant : powerPlants) {
                if (projectile.checkCollision(powerPlant)) {
                    toRemove.add(projectile);

                    // Dodaj eksplozję w miejscu trafienia
                    explosions.add(new Explosion(powerPlant.getX(), powerPlant.getY()));

                    powerPlants.remove(powerPlant);
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

    private void updateGameresources(){
        for (Harvester harvester : harvesters) { // Iteracja po liście harvesterów
            for (ResourcesSteel resource : resources) { // Iteracja po zasobach
                if (!resource.isDepleted() && harvester.getBounds().intersects(resource.getBounds())) {
                    // Harvester wydobywa zasoby
                    resource.mineResource(2); // Zmniejsz ilość zasobów o 2 na sekundę
                    collectedSteel += 2;     // Zwiększ liczbę zebranych zasobów
                }
            }
        }
        for (SteelMine steelMine : steelMines) {
            for (ResourcesSteel resource : resources) { // Iteracja po zasobach
                if (!resource.isDepleted() && steelMine.getBounds().intersects(resource.getBounds())) {
                    // Harvester wydobywa zasoby
                    resource.mineResource(2); // Zmniejsz ilość zasobów o 2 na sekundę
                    collectedSteel += 2;     // Zwiększ liczbę zebranych zasobów
                }
            }
        }
        // tu zrob by poporstu gdy jest powerplant to dodaje co 1 s 1 produ
        for (PowerPlant powerPlant : powerPlants) {
            if (totalPower < MAX_POWER) {
                totalPower += 1; // każda elektrownia dodaje 1
                if (totalPower > MAX_POWER) {
                    totalPower = MAX_POWER;
                }
            }
        }
        repaint();
    }
    //update co się dzieje w grze gdy trafi w cos dany pocisk ?
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
        // do zbierania zasobow


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
    private void cancelBuildingPlacement() {
        isPlacingBuilding = false;
        isPlacingPowerPlant = false;
        isPlacingFactory = false;
        isPlacingBarracks = false;
        isPlacingSteelMine = false;
        placementCursor = null;
        showBuilderMenu = false;     // <-- dodaj to
        selectedBuilderVehicle = null; // <-- ważne, inaczej builder nadal zaznaczony
        updateBuilderMenu();         // <-- i to, żeby zniknął interfejs
        repaint();
    }


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
        if (isPlacingBuilding && selectedBuilderVehicle != null && SwingUtilities.isLeftMouseButton(e)) {
            int mouseX = e.getX();
            int mouseY = e.getY();

            Point builderPos = selectedBuilderVehicle.getPosition();
            double distance = builderPos.distance(mouseX, mouseY);

            if (distance <= BUILD_RANGE) {
                Rectangle newBuilding = new Rectangle(mouseX, mouseY, BUILD_SIZE, BUILD_SIZE);

                boolean collision = false;
                for (PowerPlant plant : powerPlants)
                    if (plant.getBounds().intersects(newBuilding)) collision = true;

                for (SteelMine mine : steelMines)
                    if (mine.getBounds().intersects(newBuilding)) collision = true;

                for (Baracks barack : baracks)
                    if (barack.getBounds().intersects(newBuilding)) collision = true;

                for (Factory factory : factories)
                    if (factory.getBounds().intersects(newBuilding)) collision = true;

                if (!collision) {
                    switch (buildingToPlace) {
                        case POWER_PLANT:
                            powerPlants.add(new PowerPlant(mouseX, mouseY));
                            collectedSteel -= 1000;
                            totalPower += PowerPlant.getPowerGenerated();
                            btnSteelMine.setVisible(true);
                            btnSteelMine.setEnabled(true);
                            btnBaracks.setVisible(true);
                            btnBaracks.setEnabled(true);
                            btnFactory.setVisible(true);
                            btnFactory.setEnabled(true);
                            break;
                        case STEEL_MINE:
                            steelMines.add(new SteelMine(mouseX, mouseY));
                            collectedSteel -= 1500;
                            totalPower -= 100;
                            break;
                        case BARRACKS:
                            baracks.add(new Baracks(mouseX, mouseY));
                            collectedSteel -= 1500;
                            totalPower -= 100;
                            break;
                        case FACTORY:
                            factories.add(new Factory(mouseX, mouseY));
                            collectedSteel -= 2500;
                            totalPower -= 100;
                            break;
                    }

                    isPlacingBuilding = false;
                    buildingToPlace = null;
                    placementCursor = null;
                    showBuilderMenu = false;
                    updateBuilderMenu();
                    repaint();
                } else {
                    System.out.println("Kolizja! Nie można budować w tym miejscu.");
                }
            } else {
                System.out.println("Za daleko od buildera.");
            }

            return;
        }
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

                if (new Rectangle(factory.getX(), factory.getY(),115, 115).contains(e.getPoint())){
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

            if (selectedBuilderVehicle != null) {
                cancelBuildingPlacement(); // wyłącza tryb budowy i menu
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
    public void mouseMoved(MouseEvent e) {
        if (isPlacingPowerPlant && selectedBuilderVehicle != null) {
            int x = (e.getX() / BUILD_SIZE) * BUILD_SIZE;
            int y = (e.getY() / BUILD_SIZE) * BUILD_SIZE;

            placementCursor = new Rectangle(e.getX(), e.getY(), BUILD_SIZE, BUILD_SIZE);
            repaint();
        }
    }

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

        // Usunięcie wyczerpanych złóż
        resources.removeIf(ResourcesSteel::isDepleted);

        for (ResourcesSteel resource : resources) {
            resource.draw(g);
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
        for (SteelMine steelMine : steelMines) {
            steelMine.draw(g);
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
            Rectangle viewRect = getVisibleRect();

            // Używamy instancji `soldier`, NIE klasy!
            soldier.shoot(
                    g,
                    bullets,
                    enemies,
                    enemiesToo,
                    hives,
                    enemyShooters,
                    enemyHunters,
                    viewRect.x,
                    viewRect.y,
                    viewRect.width,
                    viewRect.height
            );
        }

        //budowniczy
        for (BuilderVehicle builderVehicle :builderVehicles) {
            builderVehicle.draw(g);
            builderVehicle.update(deltaTime);
            Rectangle viewRect = getVisibleRect();

            // Używamy instancji `minigunner`, NIE klasy!
            builderVehicle.shoot(
                    g,
                    bullets,
                    enemies,
                    enemiesToo,
                    hives,
                    enemyShooters,
                    enemyHunters,
                    viewRect.x,
                    viewRect.y,
                    viewRect.width,
                    viewRect.height
            );
        }
        for (Minigunner minigunner : minigunners) {
            minigunner.draw(g);

            Rectangle viewRect = getVisibleRect();

            // Używamy instancji `minigunner`, NIE klasy!
            minigunner.shoot(
                    g,
                    minigunnerBullets,
                    enemies,
                    enemiesToo,
                    hives,
                    enemyShooters,
                    enemyHunters,
                    viewRect.x,
                    viewRect.y,
                    viewRect.width,
                    viewRect.height
            );
        }
        // Rysowanie pocisków
        for (Bullet bullet : bullets) {
            bullet.draw(g);
        }

        for (BattleVehicle battleVehicle : battleVehicles){
            battleVehicle.draw(g);
            battleVehicle.update(deltaTime);
            Rectangle viewRect = getVisibleRect();

            battleVehicle.shoot(
                    g,
                    bullets,
                    enemies,
                    enemiesToo,
                    hives,
                    enemyShooters,
                    enemyHunters,
                    viewRect.x,
                    viewRect.y,
                    viewRect.width,
                    viewRect.height
            );
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
            enemyShooter.shoot(g, projectiles, soldiers, battleVehicles, powerPlants);
        }
        //budowniczy
        for (BuilderVehicle builderVehicle :builderVehicles) {
            builderVehicle.draw(g);
        }


        // Rysowanie wrogów EnemyToo
        for (EnemyToo enemyToo : enemiesToo) {
            enemyToo.draw(g);
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
        //to jest od stawiania budynkow czerwone/biale pole
        if (isPlacingBuilding && placementCursor != null && selectedBuilderVehicle != null) {
            Graphics2D g2 = (Graphics2D) g;

            int builderX = selectedBuilderVehicle.getX() + 25;
            int builderY = selectedBuilderVehicle.getY() + 25;
            int cursorCenterX = placementCursor.x + BUILD_SIZE / 2;
            int cursorCenterY = placementCursor.y + BUILD_SIZE / 2;

            double distance = Point.distance(builderX, builderY, cursorCenterX, cursorCenterY);

            g2.setColor(distance <= BUILD_RANGE ? Color.WHITE : Color.RED);
            g2.drawRect(placementCursor.x, placementCursor.y, BUILD_SIZE, BUILD_SIZE);
        }



// Wyświetlanie zasobów niezależnie od przewijania mapy
        Graphics2D g2dOverlay = (Graphics2D) g;
        Font largeFont = new Font("Arial", Font.BOLD, 15); // Czcionka Arial, pogrubiona, rozmiar 15
        g2dOverlay.setFont(largeFont);
        g2dOverlay.setColor(Color.WHITE);

// Oblicz przesunięcie widoku z JScrollPane (czyli przesunięcie mapy)
//        if (getParent() instanceof JViewport viewport) {
//            Point viewPos = viewport.getViewPosition();
//            int screenX = viewPos.x;
//            int screenY = viewPos.y;
//
//            // Tekst zawsze przyklejony do lewego górnego rogu widoku
//            g2dOverlay.drawString("Steel Collected: " + collectedSteel, screenX + 20, screenY + 30);
//            g2dOverlay.drawString("Power: " + totalPower, screenX + 20, screenY + 60);
//        }
        if (getParent() instanceof JViewport viewport) {
            Point viewPos = viewport.getViewPosition();
            int screenX = viewPos.x;
            int screenY = viewPos.y;

            btnPowerPlant.setLocation(screenX + 10, screenY + 90);
            btnSteelMine.setLocation(screenX + 10, screenY + 130);
            btnBaracks.setLocation(screenX + 10, screenY + 170);
            btnFactory.setLocation(screenX + 10, screenY + 210);

            // Dodaj te:
            btnHarvester.setLocation(screenX + 10, screenY + 90);
            btnArtylery.setLocation(screenX + 10, screenY + 130);
            btnBattleVehicle.setLocation(screenX + 10, screenY + 170);
            btnBuilderVehicle.setLocation(screenX + 10, screenY + 210);
        }

    }
}