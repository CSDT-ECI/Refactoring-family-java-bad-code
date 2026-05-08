---
layout: none
permalink: /
---
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Refactoring Family Java Project</title>
  <link rel="preconnect" href="https://fonts.googleapis.com"/>
  <link href="https://fonts.googleapis.com/css2?family=Syne:wght@400;600;700;800&family=JetBrains+Mono:wght@300;400;500&display=swap" rel="stylesheet"/>
  <style>
    :root {
      --bg:      #030712;
      --surface: #0b1120;
      --border:  rgba(99,182,255,0.12);
      --accent:  #38bdf8;
      --accent2: #818cf8;
      --accent3: #34d399;
      --text:    #e2e8f0;
      --muted:   #64748b;
    }

    *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
    html { scroll-behavior: smooth; }

    body {
      background: var(--bg);
      color: var(--text);
      font-family: 'JetBrains Mono', monospace;
      font-size: 15px;
      line-height: 1.7;
      overflow-x: hidden;
    }

    /* Grid background */
    body::before {
      content: '';
      position: fixed; inset: 0; z-index: 0;
      background-image:
        linear-gradient(rgba(56,189,248,0.04) 1px, transparent 1px),
        linear-gradient(90deg, rgba(56,189,248,0.04) 1px, transparent 1px);
      background-size: 48px 48px;
      pointer-events: none;
    }

    /* Radial glow */
    body::after {
      content: '';
      position: fixed;
      top: -20%; left: 50%; transform: translateX(-50%);
      width: 900px; height: 600px;
      background: radial-gradient(ellipse, rgba(56,189,248,0.07) 0%, transparent 70%);
      z-index: 0; pointer-events: none;
    }

    .page-wrap {
      position: relative; z-index: 1;
      max-width: 900px;
      margin: 0 auto;
      padding: 0 24px 80px;
    }

    /* ── HERO ── */
    .hero {
      padding: 80px 0 60px;
      border-bottom: 1px solid var(--border);
    }

    .hero-tag {
      display: inline-flex; align-items: center; gap: 8px;
      background: rgba(56,189,248,0.08);
      border: 1px solid rgba(56,189,248,0.2);
      color: var(--accent);
      font-size: 11px; letter-spacing: 0.15em; text-transform: uppercase;
      padding: 5px 14px; border-radius: 2px;
      margin-bottom: 28px;
    }
    .hero-tag::before {
      content: '';
      width: 6px; height: 6px; border-radius: 50%;
      background: var(--accent);
      animation: pulse 2s ease-in-out infinite;
    }
    @keyframes pulse {
      0%, 100% { opacity: 1; }
      50%       { opacity: 0.3; }
    }

    .hero h1 {
      font-family: 'Syne', sans-serif;
      font-size: clamp(2rem, 5vw, 3.2rem);
      font-weight: 800;
      line-height: 1.1;
      letter-spacing: -0.02em;
      color: #f8fafc;
      margin-bottom: 20px;
    }
    .hero h1 span {
      background: linear-gradient(90deg, var(--accent), var(--accent2));
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }

    .hero-sub {
      font-size: 13px; color: var(--muted);
      display: flex; align-items: center; gap: 20px; flex-wrap: wrap;
    }
    .hero-sub a {
      color: var(--accent); text-decoration: none;
      border-bottom: 1px solid rgba(56,189,248,0.3);
      transition: border-color 0.2s;
    }
    .hero-sub a:hover { border-color: var(--accent); }
    .sep { color: var(--border); user-select: none; }

    /* ── SECTIONS ── */
    section {
      padding: 52px 0 0;
      opacity: 0; transform: translateY(16px);
      animation: fadeUp 0.55s ease forwards;
    }
    section:nth-child(1) { animation-delay: 0.05s; }
    section:nth-child(2) { animation-delay: 0.12s; }
    section:nth-child(3) { animation-delay: 0.19s; }
    section:nth-child(4) { animation-delay: 0.26s; }
    section:nth-child(5) { animation-delay: 0.33s; }
    section:nth-child(6) { animation-delay: 0.40s; }
    section:nth-child(7) { animation-delay: 0.47s; }
    section:nth-child(8) { animation-delay: 0.54s; }
    @keyframes fadeUp {
      to { opacity: 1; transform: translateY(0); }
    }

    .section-label {
      font-size: 10px; letter-spacing: 0.2em; text-transform: uppercase;
      color: var(--accent2); margin-bottom: 20px;
      display: flex; align-items: center; gap: 10px;
    }
    .section-label::after {
      content: ''; flex: 1; height: 1px;
      background: linear-gradient(90deg, rgba(129,140,248,0.3), transparent);
    }

    p { color: #94a3b8; margin-bottom: 14px; }

    blockquote {
      border-left: 2px solid var(--accent);
      margin: 0 0 12px;
      padding: 12px 20px;
      background: rgba(56,189,248,0.05);
      border-radius: 0 4px 4px 0;
    }
    blockquote p { margin: 0; font-size: 13px; color: #94a3b8; }
    blockquote strong { color: #e2e8f0; }
    blockquote a { color: var(--accent); text-decoration: none; border-bottom: 1px solid rgba(56,189,248,0.3); }
    blockquote a:hover { border-color: var(--accent); }

    /* ── TEAM ── */
    .team-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
      gap: 14px; margin-top: 8px;
    }
    .team-card {
      background: var(--surface);
      border: 1px solid var(--border);
      border-radius: 6px; padding: 20px 22px;
      transition: border-color 0.25s, transform 0.25s;
      position: relative; overflow: hidden;
    }
    .team-card::before {
      content: '';
      position: absolute; top: 0; left: 0; right: 0; height: 2px;
      background: linear-gradient(90deg, var(--accent), var(--accent2));
      opacity: 0; transition: opacity 0.25s;
    }
    .team-card:hover { border-color: rgba(56,189,248,0.35); transform: translateY(-2px); }
    .team-card:hover::before { opacity: 1; }
    .avatar {
      width: 32px; height: 32px; border-radius: 50%;
      background: linear-gradient(135deg, var(--accent), var(--accent2));
      display: flex; align-items: center; justify-content: center;
      font-size: 11px; font-weight: 700; color: #030712;
      margin-bottom: 12px;
    }
    .team-card .name {
      font-family: 'Syne', sans-serif;
      font-weight: 600; font-size: 14px;
      color: #e2e8f0; margin-bottom: 6px;
    }
    .team-card a {
      font-size: 12px; color: var(--muted); text-decoration: none;
      transition: color 0.2s;
    }
    .team-card a:hover { color: var(--accent); }

    /* ── DOCS GRID ── */
    .docs-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
      gap: 10px; margin-top: 8px;
    }
    .doc-item {
      background: var(--surface);
      border: 1px solid var(--border);
      border-radius: 5px; padding: 14px 18px;
      display: flex; align-items: center; gap: 14px;
      text-decoration: none;
      transition: background 0.2s, border-color 0.2s;
    }
    .doc-item:hover {
      background: rgba(56,189,248,0.06);
      border-color: rgba(56,189,248,0.3);
    }
    .doc-icon {
      width: 30px; height: 30px; flex-shrink: 0;
      border: 1px solid var(--border); border-radius: 4px;
      display: flex; align-items: center; justify-content: center;
      font-size: 13px;
    }
    .doc-item span { font-size: 12.5px; color: #94a3b8; transition: color 0.2s; }
    .doc-item:hover span { color: var(--accent); }

    /* ── TABLE ── */
    .req-table { width: 100%; border-collapse: collapse; margin-top: 8px; }
    .req-table th {
      font-size: 10px; letter-spacing: 0.15em; text-transform: uppercase;
      color: var(--muted); text-align: left; padding: 10px 14px;
      border-bottom: 1px solid var(--border);
    }
    .req-table td {
      padding: 12px 14px;
      border-bottom: 1px solid rgba(99,182,255,0.06);
      color: #94a3b8; font-size: 13px;
    }
    .req-table tr:last-child td { border-bottom: none; }
    .req-table tr:hover td { background: rgba(56,189,248,0.04); }
    .badge {
      display: inline-block;
      background: rgba(52,211,153,0.1);
      border: 1px solid rgba(52,211,153,0.25);
      color: var(--accent3);
      font-size: 11px; padding: 2px 10px; border-radius: 2px;
    }
    .cmd {
      font-family: 'JetBrains Mono', monospace;
      font-size: 12px; color: var(--accent);
      background: rgba(56,189,248,0.07);
      padding: 2px 8px; border-radius: 3px;
    }

    /* ── CODE BLOCK ── */
    .code-block {
      background: #0d1526;
      border: 1px solid var(--border);
      border-radius: 6px; margin: 10px 0 0;
      overflow: hidden;
    }
    .code-header {
      display: flex; align-items: center; gap: 8px;
      padding: 10px 16px;
      border-bottom: 1px solid var(--border);
      background: rgba(0,0,0,0.3);
    }
    .dot { width: 10px; height: 10px; border-radius: 50%; }
    .dot-r { background: #ff5f56; }
    .dot-y { background: #ffbd2e; }
    .dot-g { background: #27c93f; }
    .code-lang {
      margin-left: auto; font-size: 10px;
      letter-spacing: 0.1em; color: var(--muted); text-transform: uppercase;
    }
    .code-block pre {
      padding: 16px 20px;
      font-family: 'JetBrains Mono', monospace;
      font-size: 12.5px; line-height: 1.7;
      color: #7dd3fc; overflow-x: auto;
    }
    .code-block pre .prompt { color: var(--accent2); user-select: none; }

    /* ── STEPS ── */
    .step {
      display: flex; gap: 18px;
      padding: 16px 0;
      border-bottom: 1px solid rgba(99,182,255,0.06);
    }
    .step:last-child { border-bottom: none; }
    .step-num {
      flex-shrink: 0; width: 26px; height: 26px;
      border: 1px solid var(--border); border-radius: 4px;
      display: flex; align-items: center; justify-content: center;
      font-size: 11px; font-weight: 600; color: var(--accent); margin-top: 2px;
    }
    .step-text { font-size: 13px; color: #94a3b8; }
    .step-text strong { color: #e2e8f0; }

    /* ── STACK ── */
    .stack-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
      gap: 10px; margin-top: 8px;
    }
    .stack-card {
      background: var(--surface);
      border: 1px solid var(--border);
      border-radius: 6px; padding: 18px;
      transition: border-color 0.2s, transform 0.2s;
    }
    .stack-card:hover { border-color: rgba(56,189,248,0.3); transform: translateY(-2px); }
    .stack-icon { font-size: 22px; margin-bottom: 10px; }
    .stack-name {
      font-family: 'Syne', sans-serif;
      font-weight: 700; font-size: 13px;
      color: #e2e8f0; margin-bottom: 4px;
    }
    .stack-desc { font-size: 11px; color: var(--muted); }

    /* ── FOOTER ── */
    footer {
      margin-top: 80px; padding: 28px 0;
      border-top: 1px solid var(--border);
      display: flex; align-items: center; justify-content: space-between;
      flex-wrap: wrap; gap: 12px;
    }
    footer span { font-size: 12px; color: var(--muted); }
    .footer-link {
      font-size: 12px; color: var(--accent); text-decoration: none;
      border-bottom: 1px solid rgba(56,189,248,0.3);
      transition: border-color 0.2s;
    }
    .footer-link:hover { border-color: var(--accent); }
  </style>
</head>
<body>
<div class="page-wrap">

  <!-- HERO -->
  <div class="hero">
    <div class="hero-tag">CSDT · Academic Project</div>
    <h1>Refactoring<br/><span>Family Java</span><br/>Bad Code</h1>
    <div class="hero-sub">
      <span>Sistema de árbol genealógico en Java</span>
      <span class="sep">|</span>
      <a href="https://github.com/geektrust/family-java-bad-code" target="_blank">family-java-bad-code ↗</a>
      <span class="sep">|</span>
      <span>Curso: CSDT</span>
    </div>
  </div>

  <!-- TEAM -->
  <section>
    <div class="section-label">Integrantes</div>
    <div class="team-grid">
      <div class="team-card">
        <div class="avatar">JM</div>
        <div class="name">Juan David Martínez Méndez</div>
        <a href="https://github.com/Fataltester" target="_blank">@Fataltester ↗</a>
      </div>
      <div class="team-card">
        <div class="avatar">SP</div>
        <div class="name">Samuel Alejandro Prieto Reyes</div>
        <a href="https://github.com/AlejandroPrieto82" target="_blank">@AlejandroPrieto82 ↗</a>
      </div>
      <div class="team-card">
        <div class="avatar">SG</div>
        <div class="name">Santiago Gualdrón Rincón</div>
        <a href="https://github.com/Waldron63" target="_blank">@Waldron63 ↗</a>
      </div>
    </div>
  </section>

  <!-- INTRO -->
  <section>
    <div class="section-label">Introducción</div>
    <p>
      Este repositorio contiene el código refactorizado de un proyecto Java que implementa la lógica de un árbol genealógico.
      El objetivo principal es aplicar buenas prácticas de codificación y principios de diseño de software para mejorar la calidad
      del código original, que presentaba varios <em>code smells</em>.
    </p>
    <p>
      El proceso de refactorización se centró en identificar y solucionar problemas de diseño, estructura y legibilidad,
      aplicando técnicas de refactorización y principios de código limpio.
    </p>
  </section>

  <!-- ORIGINAL REPO -->
  <section>
    <div class="section-label">Repositorio Original</div>
    <blockquote><p><strong>Organización:</strong> <a href="https://github.com/geektrust" target="_blank">@Geektrust ↗</a></p></blockquote>
    <blockquote><p><strong>Repositorio:</strong> <a href="https://github.com/geektrust/family-java-bad-code" target="_blank">family-java-bad-code ↗</a></p></blockquote>
    <blockquote><p><strong>Problemática:</strong> <a href="Geektrust-Problems1.pdf">Geektrust-Problems1.pdf ↗</a></p></blockquote>
  </section>

  <!-- INDEX -->
  <section>
    <div class="section-label">Índice de Documentos</div>
    <div class="docs-grid">
      <a class="doc-item" href="Original_README.md"><div class="doc-icon">📄</div><span>README Original</span></a>
      <a class="doc-item" href="Code_smells_Propuestas_refactoring.md"><div class="doc-icon">🔍</div><span>Code Smells y Propuestas de Refactoring</span></a>
      <a class="doc-item" href="Clean_code_XP_practice.md"><div class="doc-icon">✨</div><span>Clean Code XP Practice</span></a>
      <a class="doc-item" href="Testing_debt_Primera_entrega.md"><div class="doc-icon">🧪</div><span>Testing Debt Primera Entrega</span></a>
      <a class="doc-item" href="DevEx_DeveloperProductivity.md"><div class="doc-icon">⚡</div><span>DevEx Developer Productivity</span></a>
      <a class="doc-item" href="Procesos_de_CI.md"><div class="doc-icon">🔄</div><span>Procesos de CI</span></a>
      <a class="doc-item" href="Vibe_codings_SDD.md"><div class="doc-icon">🎯</div><span>Vibe Coding vs SDD</span></a>
      <a class="doc-item" href="Architectural_Smells.md"><div class="doc-icon">🏗️</div><span>Architectural Smells</span></a>
    </div>
  </section>

  <!-- PREREQS -->
  <section>
    <div class="section-label">Prerrequisitos</div>
    <table class="req-table">
      <thead>
        <tr>
          <th>Herramienta</th>
          <th>Versión requerida</th>
          <th>Comando de verificación</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>Java (JDK)</td>
          <td><span class="badge">11 o superior</span></td>
          <td><span class="cmd">java -version</span></td>
        </tr>
        <tr>
          <td>Gradle</td>
          <td><span class="badge">7.0 o superior</span></td>
          <td><span class="cmd">./gradlew -v</span></td>
        </tr>
      </tbody>
    </table>
  </section>

  <!-- INSTALL -->
  <section>
    <div class="section-label">Instalación</div>
    <div class="step">
      <div class="step-num">01</div>
      <div class="step-text">
        <strong>Clonar el repositorio</strong>
        <div class="code-block">
          <div class="code-header">
            <span class="dot dot-r"></span><span class="dot dot-y"></span><span class="dot dot-g"></span>
            <span class="code-lang">bash</span>
          </div>
          <pre><span class="prompt">$ </span>git clone https://github.com/tu-usuario/Refactoring-family-java-bad-code.git</pre>
        </div>
      </div>
    </div>
    <div class="step">
      <div class="step-num">02</div>
      <div class="step-text">
        <strong>Navegar al directorio del proyecto</strong>
        <div class="code-block">
          <div class="code-header">
            <span class="dot dot-r"></span><span class="dot dot-y"></span><span class="dot dot-g"></span>
            <span class="code-lang">bash</span>
          </div>
          <pre><span class="prompt">$ </span>cd Refactoring-family-java-bad-code</pre>
        </div>
      </div>
    </div>
    <div class="step">
      <div class="step-num">03</div>
      <div class="step-text">
        <strong>Construir el proyecto con Gradle</strong>
        <div class="code-block">
          <div class="code-header">
            <span class="dot dot-r"></span><span class="dot dot-y"></span><span class="dot dot-g"></span>
            <span class="code-lang">bash</span>
          </div>
          <pre><span class="prompt">$ </span>./gradlew build</pre>
        </div>
      </div>
    </div>
  </section>

  <!-- STACK -->
  <section>
    <div class="section-label">Construido Con</div>
    <div class="stack-grid">
      <div class="stack-card">
        <div class="stack-icon">☕</div>
        <div class="stack-name">Java</div>
        <div class="stack-desc">Lenguaje de programación</div>
      </div>
      <div class="stack-card">
        <div class="stack-icon">⚙️</div>
        <div class="stack-name">Gradle</div>
        <div class="stack-desc">Automatización de compilación</div>
      </div>
      <div class="stack-card">
        <div class="stack-icon">🧪</div>
        <div class="stack-name">JUnit 5</div>
        <div class="stack-desc">Framework de testing</div>
      </div>
      <div class="stack-card">
        <div class="stack-icon">🔀</div>
        <div class="stack-name">Git & GitHub</div>
        <div class="stack-desc">Control de versiones</div>
      </div>
    </div>
  </section>

  <!-- FOOTER -->
  <footer>
    <span>Proyecto académico — CSDT</span>
    <a class="footer-link" href="https://github.com/CSDT-ECI/Refactoring-family-java-bad-code" target="_blank">GitHub Repository ↗</a>
  </footer>

</div>
</body>
</html>