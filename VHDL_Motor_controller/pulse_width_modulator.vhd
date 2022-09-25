Library IEEE;
use IEEE.std_logic_1164.all; 
use IEEE.numeric_std.all;
  

entity pulse_width_modulator is
  port(
    mclk      : in std_logic; -- 100MHz
    reset     : in std_logic; -- Asynchronous reset, active high
    duty_cycle: in std_logic_vector(7 downto 0); -- Signed
    dir       : out std_logic;
    en        : out std_logic
  );
end entity;

architecture rtl of pulse_width_modulator is
  --alias dir_part : std_logic is duty_cycle(7 downto 7);
  --alias speed_part : std_logic_vector(6 downto 0) is duty_cycle(6 downto 0);

  type state is (forward_idle, reverse_idle, forward, reverse);
  signal current_state : state;
  signal next_state : state;

  signal pwm_signal : std_logic;

  component pwm is
    port (
      clk : in std_logic;
      reset : in std_logic;
      duty_cycle : in std_logic_vector(7 downto 0);
      pwm : out std_logic
    );
  end component;


begin

  PWM_MOD: pwm
    port map (
      clk => mclk,
      reset => reset,
      duty_cycle => duty_cycle,
      pwm => pwm_signal
    ); 

  current_state <= reverse_idle when reset else 
                   next_state when rising_edge(mclk); 

  NEXT_STATE_P: process(mclk, reset, duty_cycle) is
  begin
    next_state <= current_state;
    case current_state is
      when reverse_idle =>
        next_state <= reverse when signed(duty_cycle) < 0 else forward_idle;
      when forward_idle => 
        next_state <= forward when signed(duty_cycle) > 0 else reverse_idle;
      when reverse =>
        next_state <= reverse when signed(duty_cycle) < 0 else reverse_idle;
      when forward =>
        next_state <= forward when signed(duty_cycle) > 0 else forward_idle;
    end case;
  end process NEXT_STATE_P;

  OUTPUT_P: process(mclk, reset, current_state) is
  
  begin
    case current_state is
      when reverse_idle =>
        en <= '0';
        dir <= '0';
      when forward_idle =>
        en <= '0';
        dir <= '1';
      when reverse =>
        en <= pwm_signal;
        dir <= '0';
      when forward =>
        en <= pwm_signal;
        dir <= '1';
    end case;

  end process OUTPUT_P;
end architecture;










