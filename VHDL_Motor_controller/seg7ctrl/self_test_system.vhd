Library IEEE;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity self_test_system is
  port (
    mclk : in std_logic; --100MHz
    reset : in std_logic;

    c : out std_logic;
    abcdefg : out std_logic_vector(6 downto 0)
  );

end entity;

architecture struct of self_test_system is
  component selftest is
    generic (cycle_clk : integer);
    port (
      mclk : in std_logic;
      reset : in std_logic;
      disp0 : out std_logic_vector(3 downto 0);
      disp1 : out std_logic_vector(3 downto 0)
    );
  end component;

  component seg7ctrl is
    port (
      mclk : in std_logic;
      reset : in std_logic;
      d0 : in std_logic_vector(3 downto 0);
      d1 : in std_logic_vector(3 downto 0);
      abcdefg : out std_logic_vector(6 downto 0);
      c : out std_logic
    );
  end component;

  signal sys_disp0 : std_logic_vector(3 downto 0);
  signal sys_disp1 : std_logic_vector(3 downto 0);

begin

  C_SELFTEST: entity work.selftest(rtl)
    port map (
      mclk => mclk,
      reset => reset,
      disp0 => sys_disp0,
      disp1 => sys_disp1
    );

  C_SEG7CTRL: entity work.seg7ctrl(control_arch)
    port map (
      mclk => mclk,
      reset => reset,
      d0 => sys_disp0,
      d1 => sys_disp1,
      abcdefg => abcdefg,
      c => c
    );

end architecture;
