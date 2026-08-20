import { useState, type FormEvent } from 'react';
import './LandingPage.css';

const STATS = [
  { value: '2,700+', label: '전국 청년 지원 정책 (온통청년 API 기준)' },
  { value: '5', label: '대분류 — 일자리·주거·교육·복지문화·참여권리' },
  { value: '2', label: '회원 전용 알림 — 신규 정책·마감 임박' },
  { value: '4', label: '매칭 조건 — 나이·지역·학적·소득' },
];

const STEPS = [
  {
    no: '01',
    title: '조건 입력',
    desc: '나이, 지역, 학적상태, 소득을 한 페이지에서 입력합니다. 회원가입 없이 바로 시작할 수 있습니다.',
  },
  {
    no: '02',
    title: '실시간 카드 매칭',
    desc: '조건에 맞는 지원금이 카드 형태로 즉시 정렬됩니다. 대분류·중분류로 카테고리를 다시 좁혀볼 수 있습니다.',
  },
  {
    no: '03',
    title: '상세 확인 및 신청',
    desc: '상세 모달에서 지원 내용을 확인하고 신청 페이지로 바로 이동합니다. 회원가입 시 찜과 마감 임박 알림을 추가로 받을 수 있습니다.',
  },
];

const COMPARISON_ROWS = [
  { feature: '조건 매칭 · 카드 결과 조회', guest: true, member: true },
  { feature: '상세 정보 · 신청 아웃링크', guest: true, member: true },
  { feature: '찜하기', guest: false, member: true },
  { feature: '신규 정책 개시 알림', guest: false, member: true },
  { feature: '마감 임박 알림', guest: false, member: true },
];

const FAQS = [
  {
    q: '회원가입하면 무엇이 달라지나요?',
    a: '관심 있는 정책을 찜해두고, 내 조건에 맞는 신규 정책이 등록되면 알림을 받습니다. 찜한 정책이 마감 임박하면 별도로 알려드립니다.',
  },
  {
    q: '어떤 지원금 정보를 제공하나요?',
    a: '온통청년 공공 API에 등록된 정책 데이터를 기반으로 합니다. Phase 1은 서울·경기 지역을 우선 지원하며, 이후 전국으로 확대할 예정입니다.',
  },
  {
    q: '알림은 어떤 방식으로 오나요?',
    a: '출시 시점에는 이메일로 발송하며, 이후 카카오톡 알림 연동을 계획하고 있습니다.',
  },
  {
    q: '출시는 언제인가요?',
    a: '현재 개발이 진행 중입니다. 사전 알림을 신청하시면 출시 소식을 가장 먼저 안내드립니다.',
  },
];

function LogoMark() {
  return (
    <svg className="logo-mark" width="20" height="20" viewBox="0 0 20 20" fill="none">
      <rect x="1" y="1" width="18" height="18" rx="5" stroke="currentColor" strokeWidth="1.6" />
      <path
        d="M5.5 10.2 8.4 13l6.1-6.4"
        stroke="currentColor"
        strokeWidth="1.6"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  );
}

function EmailSignupForm({
  id,
  variant,
  buttonLabel,
}: {
  id?: string;
  variant: 'light' | 'dark';
  buttonLabel: string;
}) {
  const [email, setEmail] = useState('');
  const [submitted, setSubmitted] = useState(false);

  function handleSubmit(e: FormEvent) {
    e.preventDefault();
    if (!email.trim()) return;
    // TODO: 사전 알림 신청 API 연동 전이라 지금은 로컬 상태로만 처리합니다.
    setSubmitted(true);
  }

  if (submitted) {
    return (
      <p className={`signup-success signup-success--${variant}`}>
        신청 완료! 출시 소식을 가장 먼저 알려드릴게요.
      </p>
    );
  }

  return (
    <form className={`signup-form signup-form--${variant}`} onSubmit={handleSubmit}>
      <input
        id={id}
        type="email"
        required
        placeholder="이메일 주소"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
      />
      <button type="submit">{buttonLabel}</button>
    </form>
  );
}

function Header() {
  return (
    <header className="site-header">
      <div className="container header-inner">
        <a className="brand" href="#top">
          <LogoMark />
          청년픽
        </a>
        <nav className="site-nav">
          <a href="#how-it-works">이용 방법</a>
          <a href="#comparison">기능 비교</a>
          <a href="#faq">FAQ</a>
        </nav>
        <a className="btn btn--primary btn--sm" href="#signup-hero">
          알림 신청하기
        </a>
      </div>
    </header>
  );
}

function Hero() {
  return (
    <section className="hero container">
      <span className="badge">출시 전 · 사전 알림 신청</span>
      <h1>
        내게 맞는지, 쉽게 확인하고
        <br />
        알림까지 받으세요.
      </h1>
      <p className="hero-desc">
        2,700여 개 청년 지원금이 부처와 지자체마다 흩어져 있습니다. 청년픽은 나이·지역·학적·소득만
        입력하면 나에게 맞는 지원금을 카드로 바로 보여줍니다. 회원가입 시 신규 정책과 마감 임박
        알림도 받아보세요.
      </p>
      <EmailSignupForm id="signup-hero" variant="light" buttonLabel="내 지원금 확인하고 알림받기" />
      <p className="hero-note">Phase 1은 서울·경기 지역 정책부터 시작합니다.</p>
    </section>
  );
}

function Stats() {
  return (
    <section className="stats container">
      <div className="divider" />
      <div className="stats-grid">
        {STATS.map((s) => (
          <div className="stat" key={s.label}>
            <div className="stat-value">{s.value}</div>
            <div className="stat-label">{s.label}</div>
          </div>
        ))}
      </div>
      <div className="divider" />
    </section>
  );
}

function HowItWorks() {
  return (
    <section id="how-it-works" className="how container">
      <h2 className="section-title">이용 방법</h2>
      <ol className="steps">
        {STEPS.map((step) => (
          <li className="step" key={step.no}>
            <span className="step-no">{step.no}</span>
            <h3 className="step-title">{step.title}</h3>
            <p className="step-desc">{step.desc}</p>
          </li>
        ))}
      </ol>
    </section>
  );
}

function Mark({ ok }: { ok: boolean }) {
  return <span className={ok ? 'mark mark--yes' : 'mark mark--no'}>{ok ? '✓' : '✕'}</span>;
}

function ComparisonTable() {
  return (
    <section id="comparison" className="comparison container">
      <h2 className="section-title">회원·비회원 기능 비교</h2>
      <div className="comparison-table-wrap">
        <table className="comparison-table">
          <thead>
            <tr>
              <th className="col-feature">기능</th>
              <th>비회원</th>
              <th>회원</th>
            </tr>
          </thead>
          <tbody>
            {COMPARISON_ROWS.map((row) => (
              <tr key={row.feature}>
                <td className="col-feature">{row.feature}</td>
                <td>
                  <Mark ok={row.guest} />
                </td>
                <td>
                  <Mark ok={row.member} />
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
}

function FaqItem({ q, a }: { q: string; a: string }) {
  const [open, setOpen] = useState(true);
  return (
    <li className="faq-item">
      <button
        type="button"
        className="faq-question"
        aria-expanded={open}
        onClick={() => setOpen((v) => !v)}
      >
        <span>{q}</span>
        <span className="faq-toggle">{open ? '−' : '+'}</span>
      </button>
      {open && <p className="faq-answer">{a}</p>}
    </li>
  );
}

function Faq() {
  return (
    <section id="faq" className="faq container">
      <h2 className="section-title">자주 묻는 질문</h2>
      <ul className="faq-list">
        {FAQS.map((item) => (
          <FaqItem key={item.q} q={item.q} a={item.a} />
        ))}
      </ul>
    </section>
  );
}

function CtaBanner() {
  return (
    <section className="cta-banner">
      <div className="container cta-banner-inner">
        <h2>가장 먼저 청년픽을 만나보세요.</h2>
        <p>사전 알림을 신청하면 출시 소식과 초기 이용 안내를 이메일로 보내드립니다.</p>
        <EmailSignupForm variant="dark" buttonLabel="사전 알림 신청하기" />
      </div>
    </section>
  );
}

function Footer() {
  return (
    <footer className="site-footer">
      <div className="container footer-inner">
        <span>© {new Date().getFullYear()} 청년픽 (PickYouth). All rights reserved.</span>
        <span>문의: hello@pickyouth.kr</span>
      </div>
    </footer>
  );
}

export default function LandingPage() {
  return (
    <div id="top">
      <Header />
      <Hero />
      <Stats />
      <HowItWorks />
      <ComparisonTable />
      <Faq />
      <CtaBanner />
      <Footer />
    </div>
  );
}
