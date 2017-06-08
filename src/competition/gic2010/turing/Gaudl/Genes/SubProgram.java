package competition.gic2010.turing.Gaudl.Genes;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.impl.ProgramChromosome;
import org.jgap.util.CloneException;

import competition.gic2010.turing.Gaudl.gp.MarioCommand;


public class SubProgram extends MarioCommand{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3673639561584674103L;

	/**
	 * Number of subprograms. Redundant, because equal to m_types.length.
	 */
	private int m_subtrees;

	/**
	 * Minimum arity allowed during mutation of arity.
	 */
	private int m_minArity;

	/**
	 * Maximum arity allowed during mutation of arity.
	 */
	private int m_maxArity;

	/**
	 * Return types of the subprograms to excecute.
	 */
	private String[] m_types;

	private boolean m_mutateable;

	private int m_mode;

	public SubProgram(final GPConfiguration a_conf, Class[] a_types)
			throws InvalidConfigurationException {
		this(a_conf, a_types, 0, null);
	}

	/**
	 * Collage constructor: Create a sub program that has a_arity elements
	 * of the same type a_types.
	 *
	 * @param a_conf the configuration to use
	 * @param a_arity number of children in the collage
	 * @param a_types uniform type of all children
	 * @throws org.jgap.InvalidConfigurationException
	 *
	 * @author Klaus Meffert
	 * @since 3.4
	 */
	public SubProgram(final GPConfiguration a_conf, int a_arity, Class a_types)
			throws InvalidConfigurationException {
		this(a_conf, a_arity, a_types, false);
	}

	/**
	 * Collage constructor: Create a sub program that has a_arity elements
	 * of the same type a_types.
	 *
	 * @param a_conf the configuration to use
	 * @param a_arity number of children in the collage
	 * @param a_types uniform type of all children
	 * @param a_mutateable true: allow mutation of the sub program, i.e., the
	 * number of children (=arity) may be varied automatically during evolution
	 * @throws org.jgap.InvalidConfigurationException
	 *
	 * @author Klaus Meffert
	 * @since 3.4
	 */
	public SubProgram(final GPConfiguration a_conf, int a_arity, Class a_types,
			boolean a_mutateable)
					throws InvalidConfigurationException {
		this(a_conf, a_arity, a_types, a_arity, a_arity + 5, a_mutateable);
	}

	public SubProgram(final GPConfiguration a_conf, int a_arity, Class a_types,
			int a_minArity, int a_maxArity, boolean a_mutateable)
					throws InvalidConfigurationException {
		super(a_conf, a_arity, a_types, 0, null);
		if (a_arity < 1) {
			throw new IllegalArgumentException("Arity must be >= 1");
		}
		if (a_minArity > a_arity) {
			throw new IllegalArgumentException("Arity must not be smaller than"
					+ " min. arity");
		}
		if (a_maxArity < a_arity) {
			throw new IllegalArgumentException("Arity must not be bigger than"
					+ " max. arity");
		}
		m_mode = 2;
		m_types = new String[a_arity];
		for (int i = 0; i < a_arity; i++) {
			m_types[i] = a_types.getName();
		}
		m_subtrees = a_arity;
		m_mutateable = a_mutateable;
		m_minArity = a_minArity;
		m_maxArity = a_maxArity;
	}

	public SubProgram(final GPConfiguration a_conf, Class[] a_types,
			boolean a_mutateable)
					throws InvalidConfigurationException {
		this(a_conf, a_types, 0, null, a_mutateable);
	}

	public SubProgram() throws InvalidConfigurationException {
		this(GPGenotype.getStaticGPConfiguration(),2,CommandGene.VoidClass,true);
	}

	public SubProgram(final GPConfiguration a_conf, Class[] a_types,
			int a_subReturnType, int[] a_subChildTypes)
					throws InvalidConfigurationException {
		this(a_conf, a_types, a_subReturnType, a_subChildTypes, false);
	}

	public SubProgram(final GPConfiguration a_conf, Class[] a_types,
			int a_subReturnType, int[] a_subChildTypes, boolean
			a_mutateable)
					throws InvalidConfigurationException {
		super(a_conf, a_types.length, a_types[a_types.length - 1], a_subReturnType,
				a_subChildTypes);
		if (a_types.length < 1) {
			throw new IllegalArgumentException("Number of subtrees must be >= 1");
		}
		m_mode = 1;
		m_minArity = a_types.length;
		m_maxArity = m_minArity + 5;
		setTypes(a_types);
		m_subtrees = a_types.length;
		m_mutateable = a_mutateable;
	}


	public String toString() {
		String ret = "sub[";
		for (int i = 1; i < m_subtrees; i++) {
			ret += "&" + i + " --> ";
		}
		ret += "&" + m_subtrees + "]";
		return ret;
	}

	/**
	 * @return textual name of this command
	 *
	 * @author Klaus Meffert
	 * @since 3.2
	 */
	public String getName() {
		return "Sub program";
	}

	public int execute_int(ProgramChromosome c, int n, Object[] args) {
		check(c);
		int value = -1;
		for (int i = 0; i < m_subtrees; i++) {
			if (i < m_subtrees - 1) {
				c.execute_void(n, i, args);
			}
			else {
				value = c.execute_int(n, i, args); /**@todo evaluate m_types*/
			}
			//		      if (i < m_subtrees - 1) {
			//		        ( (GPConfiguration) getConfiguration()).storeThruput(i,
			//		            new Integer(value));
			//		      }
		}
		return value;
	}

	public void execute_void(ProgramChromosome c, int n, Object[] args) {
		check(c);
		for (int i = 0; i < m_subtrees; i++) {
			c.execute_void(n, i, args); /**@todo evaluate m_types*/
		}
	}

	public long execute_long(ProgramChromosome c, int n, Object[] args) {
		check(c);
		long value = -1;
		for (int i = 0; i < m_subtrees; i++) {
			value = c.execute_long(n, i, args);
		}
		return value;
	}

	public float execute_float(ProgramChromosome c, int n, Object[] args) {
		check(c);
		float value = -1;
		for (int i = 0; i < m_subtrees; i++) {
			value = c.execute_float(n, i, args);
		}
		return value;
	}

	public double execute_double(ProgramChromosome c, int n, Object[] args) {
		check(c);
		double value = -1;
		for (int i = 0; i < m_subtrees; i++) {
			value = c.execute_double(n, i, args);
		}
		return value;
	}

	public Object execute_object(ProgramChromosome c, int n, Object[] args) {
		check(c);
		Object value = null;
		for (int i = 0; i < m_subtrees; i++) {
			value = c.execute_object(n, i, args);
		}
		return value;
	}

	public boolean isValid(ProgramChromosome a_program) {
		return true;
	}

	public void setTypes(Class[] a_Types) {
		String[] types = new String[a_Types.length];

		for (int i = 0; i< a_Types.length;i++)
			types[i] = a_Types[i].getName();


		m_types = types;
	}

	public Class[] getTypes() {
		Class[] types = new Class[m_types.length];

		for (int i = 0; i<m_types.length;i++)
			try {
				types[i] = Class.forName(m_types[i]);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}


		return types;
	}

	public Class getChildType(IGPProgram a_ind, int a_chromNum) {
		try {
			return Class.forName(m_types[a_chromNum]);
		} catch (ArrayIndexOutOfBoundsException aex) {
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * The compareTo-method.
	 *
	 * @param a_other the other object to compare
	 * @return -1, 0, 1
	 *
	 * @author Klaus Meffert
	 * @since 3.0
	 */
	public int compareTo(Object a_other) {
		int result = super.compareTo(a_other);
		if (result != 0) {
			return result;
		}
		SubProgram other = (SubProgram) a_other;
		return new CompareToBuilder()
		.append(m_types, other.m_types)
		.toComparison();
	}

	/**
	 * The equals-method.
	 *
	 * @param a_other the other object to compare
	 * @return true if the objects are seen as equal
	 *
	 * @author Klaus Meffert
	 * @since 3.0
	 */
	public boolean equals(Object a_other) {
		try {
			SubProgram other = (SubProgram) a_other;
			return super.equals(a_other) && new EqualsBuilder()
			.append(m_types, other.m_types)
			.isEquals();
		} catch (ClassCastException cex) {
			return false;
		}
	}

	/**
	 * @return deep clone of this instance
	 *
	 * @author Klaus Meffert
	 * @since 3.2
	 */
	public Object clone() {
		try {
			//		      int[] subChildTypes = getSubChildTypes();
			//		      if (subChildTypes != null) {
			//		        subChildTypes = (int[]) subChildTypes.clone();
			//		      }
			//		      SubProgram result = new SubProgram(getGPConfiguration(), m_types,
			//		          getSubReturnType(), subChildTypes, m_mutateable);
			//		      result.m_subtrees = m_subtrees;
			//		      result.m_types = (Class[]) m_types.clone();
			//		      return result;
			SubProgram result;
			if (m_mode == 1) {
				// First way of construction.
				// --------------------------
				Class[] types = new Class[m_subtrees];
				for (int i = 0; i < m_subtrees; i++) {
					types[i] = Class.forName(m_types[m_types.length - 1]);
				}
				int[] subChildTypes = getSubChildTypes();
				if (subChildTypes != null) {
					subChildTypes = (int[]) subChildTypes.clone();
				}
				result = new SubProgram(getGPConfiguration(), types,
						getSubReturnType(), subChildTypes, m_mutateable);
			}
			else {
				// Second way of construction.
				// ---------------------------
				result = new SubProgram(getGPConfiguration(), m_subtrees, Class.forName(m_types[0]),
						m_minArity, m_maxArity, m_mutateable);
			}
			return result;
		} catch (Throwable t) {
			throw new CloneException(t);
		}
	}

	public CommandGene applyMutation(int index, double a_percentage)
			throws InvalidConfigurationException {
		if (!m_mutateable) {
			return this;
		}
		org.jgap.RandomGenerator randomGen = getGPConfiguration().
				getRandomGenerator();
		double random = randomGen.nextDouble();
		if (random < a_percentage) {
			return applyMutation();
		}
		return this;
	}

	/**@todo use dynamizeArity instead!*/

	/**
	 * @return mutated command gene
	 * @throws InvalidConfigurationException
	 */
	public CommandGene applyMutation()
			throws InvalidConfigurationException {
		int size = getGPConfiguration().getRandomGenerator().nextInt(m_maxArity + 1 -
				m_minArity) + m_minArity;
		if (m_types.length == size) {
			return this;
		}
		SubProgram result = null;
		if (m_mode == 1) {
			// First way of construction.
			// --------------------------
			Class[] types = new Class[size];
			for (int i = 0; i < size; i++) {
				try {
					types[i] = Class.forName(m_types[m_types.length - 1]);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			int[] subChildTypes = getSubChildTypes();
			if (subChildTypes != null) {
				subChildTypes = (int[]) subChildTypes.clone();
			}
			result = new SubProgram(getGPConfiguration(), types,
					getSubReturnType(), subChildTypes, m_mutateable);
		}
		else {
			// Second way of construction.
			// ---------------------------
			try {
				result = new SubProgram(getGPConfiguration(), size, Class.forName(m_types[0]),
                        m_minArity, m_maxArity, m_mutateable);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
