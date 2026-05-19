(ns plotter)

;; const
(def carriage-up :up)
(def carriage-down :down)

(def color-names
  {:black "чорный"
   :red   "красный"
   :green "зелёный"})

;; functions
(defn printer [s]
  (println s))

(defn draw-line [prt from to color]
  (prt (format "...Чертим линию из (%.0f, %.0f) в (%.0f, %.0f) используя %s цвет."
               (double (:x from)) (double (:y from))
               (double (:x to))   (double (:y to))
               (color-names color))))

(defn calc-new-position [distance angle current]
  (let [rad (* angle (/ Math/PI 180.0))
        new-x (+ (:x current) (* distance (Math/cos rad)))
        new-y (+ (:y current) (* distance (Math/sin rad)))]
    {:x (Math/round new-x)
     :y (Math/round new-y)}))

;; main commands
(defn move [prt distance state]
  (let [new-pos (calc-new-position distance (:angle state) (:position state))]
    (if (= (:carriage-state state) carriage-down)
      (draw-line prt (:position state) new-pos (:color state))
      (prt (format "Передвигаем на %d от точки (%.0f, %.0f)"
                   (int distance)
                   (double (:x (:position state)))
                   (double (:y (:position state))))))
    (assoc state :position new-pos)))

(defn turn [prt angle state]
  (prt (format "Поворачиваем на %.1f градусов" (double angle)))
  (assoc state :angle (mod (+ (:angle state) angle) 360.0)))

(defn carriage-up [prt state]
  (prt "Поднимаем каретку")
  (assoc state :carriage-state carriage-up))

(defn carriage-down [prt state]
  (prt "Опускаем каретку")
  (assoc state :carriage-state carriage-down))

(defn set-color [prt color state]
  (prt (format "Устанавливаем %s цвет линии." (color-names color)))
  (assoc state :color color))

(defn set-position [prt position state]
  (prt (format "Устанавливаем позицию каретки в (%.0f, %.0f)."
               (double (:x position)) (double (:y position))))
  (assoc state :position position))

;; drawing functions
(defn draw-triangle [prt size state]
  (->> state
       (carriage-down prt)
       (move prt size)
       (turn prt 120.0)
       (move prt size)
       (turn prt 120.0)
       (move prt size)
       (turn prt 120.0)
       (carriage-up prt)))

(defn draw-square [prt size state]
  (->> state
       (carriage-down prt)
       (move prt size)
       (turn prt 90.0)
       (move prt size)
       (turn prt 90.0)
       (move prt size)
       (turn prt 90.0)
       (move prt size)
       (turn prt 90.0)
       (carriage-up prt)))

;; boot
(defn run []
  (let [initial-state {:position       {:x 0.0 :y 0.0}
                       :angle          0.0
                       :color          :black
                       :carriage-state carriage-up}]
    
    (println "=== Виртуальный плоттер запущен ===\n")
    
    ;; Точно такой же порядок действий, как в plotter.ts
    (->> initial-state
         (draw-triangle printer 100.0)
         (set-position printer {:x 10.0 :y 10.0})
         (set-color printer :red)
         (draw-square printer 80.0))
    
    (println "\n=== Чертёж завершён ===")))


