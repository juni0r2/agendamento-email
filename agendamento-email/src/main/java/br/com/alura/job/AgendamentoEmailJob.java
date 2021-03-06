package br.com.alura.job;

import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.Queue;

import br.com.alura.entidade.AgendamentoEmail;
import br.com.alura.servico.AgendamentoEmailServico;

@Singleton
public class AgendamentoEmailJob {

	@Inject
	AgendamentoEmailServico agendamentoEmailServico;
	
	@Inject
	@JMSConnectionFactory("java:jboss/DefaultJMSConnectionFactory")
	JMSContext context;

	@Resource(mappedName = "java:/jms/queue/EmailQueue")
	Queue fila;
	
	@Schedule(hour = "*", minute = "*", second = "*/10")
	public void enviarEmail() {
		List<AgendamentoEmail> listaNaoAgendados = agendamentoEmailServico.listarNaoAgendado();
		listaNaoAgendados.forEach(email -> {
			context.createProducer().send(fila, email);
			agendamentoEmailServico.alterar(email);
		});
	}
}
