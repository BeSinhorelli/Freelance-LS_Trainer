import { useState, useEffect } from 'react';
import type { FormEvent } from 'react';
import toast from 'react-hot-toast';
import { api } from '../api/client';
import { useAuth } from '../hooks/useAuth';
import styles from './PerfilPage.module.css';

export function PerfilPage() {
  const { professor, login } = useAuth();
  
  // Dados Básicos
  const [nome, setNome] = useState('');
  const [email, setEmail] = useState('');
  const [loadingDados, setLoadingDados] = useState(false);

  // Senha
  const [senhaAtual, setSenhaAtual] = useState('');
  const [novaSenha, setNovaSenha] = useState('');
  const [loadingSenha, setLoadingSenha] = useState(false);

  useEffect(() => {
    if (professor) {
      setNome(professor.nome);
      setEmail(professor.email);
    }
  }, [professor]);

  async function handleAtualizarDados(e: FormEvent) {
    e.preventDefault();
    setLoadingDados(true);

    try {
      await api.put('/professores/me', { nome, email });
      toast.success('Dados atualizados com sucesso!');
      // Recarrega a página para atualizar o nome no topo
      setTimeout(() => window.location.reload(), 1000);
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Erro ao atualizar dados.');
    } finally {
      setLoadingDados(false);
    }
  }

  async function handleAtualizarSenha(e: FormEvent) {
    e.preventDefault();
    setLoadingSenha(true);

    try {
      await api.put('/professores/me/senha', { senhaAtual, novaSenha });
      toast.success('Senha alterada com sucesso!');
      setSenhaAtual('');
      setNovaSenha('');
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Erro ao alterar senha.');
    } finally {
      setLoadingSenha(false);
    }
  }

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <h1>Meu Perfil</h1>
        <p>Gerencie suas informações de acesso e segurança.</p>
      </div>

      <div className={styles.grid}>
        {/* Formulário de Dados Básicos */}
        <form onSubmit={handleAtualizarDados} className={styles.card}>
          <h2>Dados Básicos</h2>
          
          <div className={styles.field}>
            <label>Nome Completo</label>
            <input 
              type="text" 
              value={nome} 
              onChange={e => setNome(e.target.value)} 
              required 
              placeholder="Seu nome"
            />
          </div>

          <div className={styles.field}>
            <label>E-mail de Acesso</label>
            <input 
              type="email" 
              value={email} 
              onChange={e => setEmail(e.target.value)} 
              required 
              placeholder="seu@email.com"
            />
          </div>

          <button type="submit" disabled={loadingDados} className={styles.btnPrimary}>
            {loadingDados ? 'Salvando...' : 'Salvar Alterações'}
          </button>
        </form>

        {/* Formulário de Senha */}
        <form onSubmit={handleAtualizarSenha} className={styles.card}>
          <h2>Segurança</h2>
          
          <div className={styles.field}>
            <label>Senha Atual</label>
            <input 
              type="password" 
              value={senhaAtual} 
              onChange={e => setSenhaAtual(e.target.value)} 
              required 
              placeholder="Digite sua senha atual"
            />
          </div>

          <div className={styles.field}>
            <label>Nova Senha</label>
            <input 
              type="password" 
              value={novaSenha} 
              onChange={e => setNovaSenha(e.target.value)} 
              required 
              minLength={6}
              placeholder="Mínimo de 6 caracteres"
            />
          </div>

          <button type="submit" disabled={loadingSenha} className={styles.btnDanger}>
            {loadingSenha ? 'Alterando...' : 'Alterar Senha'}
          </button>
        </form>
      </div>
    </div>
  );
}