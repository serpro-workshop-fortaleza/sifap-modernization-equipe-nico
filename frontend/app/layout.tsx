import type { Metadata } from "next";
import { CabecalhoGovBr, RodapeGovBr } from "@/components/govbr-shell";
import "./globals.css";

export const metadata: Metadata = {
  title: "SIFAP — Catálogo de Programas Sociais",
  description: "Inclusão e consulta de programas sociais do SIFAP",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="pt-BR">
      <body className="flex min-h-screen flex-col antialiased">
        <CabecalhoGovBr />
        <div className="flex flex-1 flex-col">{children}</div>
        <RodapeGovBr />
      </body>
    </html>
  );
}
